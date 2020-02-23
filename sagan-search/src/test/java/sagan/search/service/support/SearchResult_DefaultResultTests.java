package sagan.search.service.support;

import sagan.search.service.SearchResult;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class SearchResult_DefaultResultTests {

    @Test
    public void resultText_returnsSummary_whenNoHighlight() {
        SearchResult result =
                new SearchResult("id", "title", "subTitle", "summary", "path", "site", null, "original search term");
        assertThat(result.getDisplayText(), equalTo("summary"));
    }

    @Test
    public void resultText_returnsHighlight_whenPresent() {
        SearchResult result =
                new SearchResult("id", "title", "subTitle", "summary", "path", "site", "highlight",
                        "original search term");
        assertThat(result.getDisplayText(), equalTo("highlight"));
    }
}
