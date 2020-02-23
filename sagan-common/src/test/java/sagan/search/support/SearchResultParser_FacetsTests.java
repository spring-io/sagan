package sagan.search.support;

import sagan.search.service.SearchFacet;
import sagan.search.service.SearchResults;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import io.searchbox.client.JestResult;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class SearchResultParser_FacetsTests {

    private static String RESULT_STRING = "{\n" +
            "  \"took\": 185,\n" +
            "  \"timed_out\": false,\n" +
            "  \"_shards\": {\n" +
            "    \"total\": 1,\n" +
            "    \"successful\": 1,\n" +
            "    \"failed\": 0\n" +
            "  },\n" +
            "  \"hits\": {\n" +
            "    \"total\": 0,\n" +
            "    \"max_score\": 0.5340888,\n" +
            "    \"hits\": []\n" +
            "  },\n" +
            "  \"facets\": {\n" +
            "    \"facet_paths_result\": {\n" +
            "      \"_type\": \"terms\",\n" +
            "      \"missing\": 0,\n" +
            "      \"total\": 15,\n" +
            "      \"other\": 0,\n" +
            "      \"terms\": [{\n" +
            "        \"term\": \"Blog\",\n" +
            "        \"count\": 2\n" +
            "      }, {\n" +
            "        \"term\": \"Guides\",\n" +
            "        \"count\": 3\n" +
            "      }, {\n" +
            "        \"term\": \"Guides/GettingStarted\",\n" +
            "        \"count\": 2\n" +
            "      }, {\n" +
            "        \"term\": \"Guides/Tutorials\",\n" +
            "        \"count\": 1\n" +
            "      }, {\n" +
            "        \"term\": \"Guides/Reference Apps\",\n" +
            "        \"count\": 1\n" +
            "      }, {\n" +
            "        \"term\": \"Projects/Spring Framework\",\n" +
            "        \"count\": 1\n" +
            "      }, {\n" +
            "        \"term\": \"Projects/Spring Framework/3.1.3.RELEASE\",\n" +
            "        \"count\": 1\n" +
            "      }, {\n" +
            "        \"term\": \"Projects/Spring Framework/3.2.3.RELEASE\",\n" +
            "        \"count\": 1\n" +
            "      }, {\n" +
            "        \"term\": \"Projects/Spring Framework/4.0.0.M1\",\n" +
            "        \"count\": 1\n" +
            "      },\n" +
            "{\"term\":\"Projects/Spring Security\",\"count\":797},\n" +
            "{\"term\":\"Projects/Spring Security/3.1.4.RELEASE\",\"count\":157},\n" +
            "{\"term\":\"Projects/Spring Security/3.2.0.M2\",\"count\":640},\n" +
            "{\"term\":\"Projects/Spring Security Kerberos\",\"count\":12},\n" +
            "{\"term\":\"Projects/Spring Security Kerberos/1.0.0.CI-SNAPSHOT\",\"count\":12},\n" +
            "{\"term\":\"Projects/Spring Security SAML\",\"count\":105},\n" +
            "{\"term\":\"Projects/Spring Security SAML/1.0.0.RC2\",\"count\":105}," +
            "      {\n" +
            "        \"term\": \"Projects\",\n" +
            "        \"count\": 1\n" +
            "      }]\n" +
            "    }\n" +
            "  }\n" +
            "}\n";

    private Gson gson = new Gson();
    private SearchResultParser searchResultParser;
    private SearchResults searchResults;

    @Before
    public void setup() {
        JsonParser jsonParser = new JsonParser();
        searchResultParser = new SearchResultParser();
        JestResult jestResult = new JestResult(gson);
        jestResult.setJsonObject(jsonParser.parse(RESULT_STRING).getAsJsonObject());

        Pageable pageable = new PageRequest(1, 10);
        searchResults = searchResultParser.parseResults(jestResult, pageable, "search term");
    }

    @Test
    public void returnTopLevelFacets() {
        List<SearchFacet> facets = searchResults.getFacets();
        assertThat(facets.size(), equalTo(3));
        assertThat(facets.get(0).getName(), equalTo("Blog"));
        assertThat(facets.get(1).getName(), equalTo("Guides"));
        assertThat(facets.get(2).getName(), equalTo("Projects"));
    }

    @Test
    public void returnNestedFacets() {
        SearchFacet guidesFacet = searchResults.getFacets().get(1);
        assertThat(guidesFacet.getFacets().size(), equalTo(3));

        SearchFacet projectsFacet = searchResults.getFacets().get(2);
        List<SearchFacet> projectsFacetFacets = projectsFacet.getFacets();
        assertThat(projectsFacetFacets.size(), equalTo(4));
        assertThat(projectsFacetFacets.get(0).getName(), equalTo("Spring Framework"));
        assertThat(projectsFacetFacets.get(1).getName(), equalTo("Spring Security"));
        assertThat(projectsFacetFacets.get(2).getName(), equalTo("Spring Security Kerberos"));
        assertThat(projectsFacetFacets.get(3).getName(), equalTo("Spring Security SAML"));
    }

}
