package sagan.search.support;

import io.searchbox.action.Action;
import sagan.search.SearchException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import io.searchbox.indices.mapping.PutMapping;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class SearchIndexSetup {

    private static Log logger = LogFactory.getLog(JestSearchService.class);
    private final JestClient jestClient;
    private String index;

    public SearchIndexSetup(JestClient jestClient, String index) {
        this.jestClient = jestClient;
        this.index = index;
    }

    public void deleteIndex() {
        execute(new DeleteIndex.Builder(index).build());
    }

    public void createIndex() {
        CreateIndex.Builder builder = new CreateIndex.Builder(index);

        builder.settings(loadSettings());
        execute(builder.build());

        createMappings();
    }

    public void createMappings() {
        try {

            File mappingsDir = new ClassPathResource("/elasticsearch/mappings", getClass()).getFile();
            for (final File fileEntry : mappingsDir.listFiles()) {
                String filenameBase = fileEntry.getName().replaceAll("\\.json$", "");
                String mappingJson = StreamUtils.copyToString(new FileInputStream(fileEntry), Charset.forName("UTF-8"));
                PutMapping.Builder mapping = new PutMapping.Builder(index, filenameBase, mappingJson);
                execute(mapping.build());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, String> loadSettings() {
        Map<String, String> settings = new HashMap<>();
        try {
            InputStream settingsStream =
                    new ClassPathResource("/elasticsearch/settings.json", getClass()).getInputStream();
            JsonParser jsonParser = new JsonParser();
            JsonElement root = jsonParser.parse(new InputStreamReader(settingsStream, Charset.forName("UTF-8")));

            for (Map.Entry<String, JsonElement> entry : root.getAsJsonObject().entrySet()) {
                settings.put(entry.getKey(), entry.getValue().getAsString());
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return settings;
    }

    private JestResult execute(Action action) {
        try {
            JestResult result = jestClient.execute(action);
            logger.debug(result.getJsonString());
            return result;
        } catch (Exception e) {
            throw new SearchException(e);
        }
    }

}
