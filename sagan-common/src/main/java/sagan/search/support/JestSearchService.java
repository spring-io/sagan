package sagan.search.support;

import sagan.search.SearchException;
import sagan.search.service.SearchResults;
import sagan.search.service.SearchService;
import sagan.search.types.SearchEntry;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.index.query.FilteredQueryBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import io.searchbox.action.Action;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Delete;
import io.searchbox.core.DeleteByQuery;
import io.searchbox.core.Index;
import io.searchbox.core.Search;

import com.google.gson.Gson;

public class JestSearchService implements SearchService {

    private static Log logger = LogFactory.getLog(JestSearchService.class);

    private final JestClient jestClient;
    private final Gson gson;

    private boolean useRefresh = false;
    private SearchResultParser searchResultParser;

    @Value("${elasticsearch.client.index}")
    private String index;

    @Autowired
    public JestSearchService(JestClient jestClient, SearchResultParser searchResultParser, Gson gson) {
        this.jestClient = jestClient;
        this.searchResultParser = searchResultParser;
        this.gson = gson;
    }

    public String getIndexName() {
        return index;
    }

    @Override
    public void saveToIndex(SearchEntry entry) {
        Index.Builder indexEntryBuilder = new Index.Builder(entry).id(entry.getId()).index(index).type(entry.getType());

        if (useRefresh) {
            indexEntryBuilder.refresh(true);
        }
        logger.debug("Indexing " + entry.getPath());
        execute(indexEntryBuilder.build());
    }

    @Override
    public SearchResults search(String term, Pageable pageable, List<String> filter) {
        Search.Builder searchBuilder;
        if (StringUtils.isEmpty(term)) {
            searchBuilder = SaganQueryBuilders.forEmptyQuery(pageable, filter);
        } else {
            searchBuilder = SaganQueryBuilders.fullTextSearch(term, pageable, filter);
        }
        searchBuilder.addIndex(index);
        Search search = searchBuilder.build();
        logger.debug(search.getData(this.gson));
        JestResult jestResult = execute(search);
        return searchResultParser.parseResults(jestResult, pageable, term);
    }

    public void setUseRefresh(boolean useRefresh) {
        this.useRefresh = useRefresh;
    }

    public void removeFromIndex(SearchEntry entry) {
        Delete delete = new Delete.Builder(entry.getId())
                .index(index)
                .type(entry.getType())
                .build();

        execute(delete);
    }

    public void removeOldProjectEntriesFromIndex(String projectId, List<String> supportedVersions) {
        FilteredQueryBuilder builder = SaganQueryBuilders.matchUnsupportedProjectEntries(projectId, supportedVersions);
        String query = SaganQueryBuilders.wrapQuery(builder.toString());
        execute(new DeleteByQuery.Builder(query).build());
    }

    private JestResult execute(Action action) {
        try {
            JestResult result = jestClient.execute(action);
            logger.debug(result.getJsonString());
            if (!result.isSucceeded()) {
                logger.warn("Failed to execute Elastic Search action: " + result.getErrorMessage()
                        + " " + result.getJsonString());
            }
            return result;
        } catch (Exception e) {
            throw new SearchException(e);
        }
    }
}
