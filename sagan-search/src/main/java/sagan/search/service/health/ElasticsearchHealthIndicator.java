package sagan.search.service.health;

import com.google.gson.JsonObject;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.indices.Stats;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ElasticsearchHealthIndicator extends AbstractHealthIndicator {


    private final JestClient jestClient;

    private final List<String> indices;

    public ElasticsearchHealthIndicator(JestClient jestClient) {
        this.jestClient = jestClient;
        this.indices = Collections.singletonList("_all");
    }

    public ElasticsearchHealthIndicator(JestClient jestClient, String... indices) {
        this.jestClient = jestClient;
        this.indices = Arrays.asList(indices);
    }

    @Override
    protected void doHealthCheck(Builder builder) throws Exception {

        JestResult result = jestClient.execute(new Stats.Builder().addIndex(this.indices).build());
        JsonObject map = result.getJsonObject();

        if (result.isSucceeded()) {
            builder.up();
            for (String indexName : this.indices) {
                fillStatsForIndex(indexName, map, builder);
            }
        }
        else {
            builder.down();
        }
    }

    private void fillStatsForIndex(String indexName, JsonObject map, Builder builder) {

        JsonObject indexStats = map.getAsJsonObject("indices").getAsJsonObject(indexName);
        if (indexStats != null) {
            JsonObject primaries = indexStats.getAsJsonObject("primaries");
            builder.withDetail(indexName + "." + "docs_count",
                    primaries.getAsJsonObject("docs").get("count").getAsString());
            builder.withDetail(indexName + "." + "docs_deleted",
                    primaries.getAsJsonObject("docs").get("deleted").getAsString());
            builder.withDetail(indexName + "." + "store_size",
                    primaries.getAsJsonObject("store").get("size_in_bytes").getAsString());
            builder.withDetail(indexName + "." + "query_total",
                    primaries.getAsJsonObject("search").get("query_total").getAsString());
        }
    }
}
