package sagan.search.support;

import sagan.search.service.SearchFacet;
import sagan.search.service.SearchResult;
import sagan.search.service.SearchResults;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import io.searchbox.client.JestResult;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class SearchResultParser {

    public SearchResults parseResults(JestResult jestResult, Pageable pageable, String originalSearchTerm) {
        JsonObject response = jestResult.getJsonObject();
        PageImpl<SearchResult> page = prepareResults(response, pageable, originalSearchTerm);
        List<SearchFacet> facets = prepareFacets(response);
        return new SearchResults(page, facets);
    }

    private PageImpl<SearchResult> prepareResults(JsonObject response, Pageable pageable, String originalSearchTerm) {
        JsonObject hits = response.getAsJsonObject("hits");
        int totalResults = 0;
        List<SearchResult> results = Collections.emptyList();

        if (hits != null) {
            JsonArray resultsArray = hits.getAsJsonArray("hits");
            results = prepareResults(resultsArray, originalSearchTerm);
            totalResults = hits.get("total").getAsInt();
        }

        PageImpl<SearchResult> page = new PageImpl<>(results, pageable, totalResults);
        return page;
    }

    private List<SearchFacet> prepareFacets(JsonObject response) {
        JsonObject facets = response.getAsJsonObject("facets");
        if (facets == null) {
            return Collections.emptyList();
        }
        JsonObject pathsResult = facets.getAsJsonObject("facet_paths_result");
        List<JsonElement> terms = sortedTermElements(pathsResult.getAsJsonArray("terms"));

        SearchFacetTreeBuilder builder = new SearchFacetTreeBuilder();

        for (JsonElement element : terms) {
            JsonObject jsonObject = element.getAsJsonObject();
            String term = jsonObject.get("term").getAsString();
            int count = jsonObject.get("count").getAsInt();
            builder.addTerm(term, count);
        }

        return builder.build();
    }

    private List<JsonElement> sortedTermElements(JsonArray termArray) {
        ArrayList<JsonElement> list = Lists.newArrayList(termArray);
        Collections.sort(list, (o1, o2) -> {
            String term1 = o1.getAsJsonObject().get("term").getAsString().replace(" ", "");
            String term2 = o2.getAsJsonObject().get("term").getAsString().replace(" ", "");
            return term1.compareTo(term2);
        });
        return list;
    }

    private ArrayList<SearchResult> prepareResults(JsonArray hits, String originalSearchTerm) {
        ArrayList<SearchResult> results = new ArrayList<>();
        for (JsonElement element : hits) {
            JsonObject hit = element.getAsJsonObject();
            String id = hit.get("_id").getAsString();

            JsonObject source = hit.getAsJsonObject("_source");
            String title = source.get("title").getAsString();
            String url = source.get("path").getAsString();
            String type = hit.get("_type").getAsString();
            String summary = source.get("summary").getAsString();
            String subtitle = safelyLookupFieldString(source, "subTitle");

            String highlight = extractHighlight(hit);

            SearchResult result =
                    new SearchResult(id, title, subtitle, summary, url, type, highlight, originalSearchTerm);
            results.add(result);
        }
        return results;
    }

    private String safelyLookupFieldString(JsonObject source, String field) {
        JsonElement fieldElement = source.get(field);
        return (fieldElement != null) ? fieldElement.getAsString() : "";
    }

    private String extractHighlight(JsonObject hit) {
        String summary = null;
        JsonObject highlight = hit.getAsJsonObject("highlight");

        if (highlight != null) {
            JsonArray rawContent = highlight.getAsJsonArray("rawContent");
            summary = rawContent.get(0).getAsString();
        }

        return summary;
    }
}
