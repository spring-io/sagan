package sagan.search.support;

import sagan.search.service.SearchFacet;
import sagan.search.service.SearchResult;
import sagan.search.service.SearchResults;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.ExtendedModelMap;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.*;

public class SearchControllerTests {

    @Mock
    private JestSearchService searchService;

    private SearchController controller;
    private ExtendedModelMap model = new ExtendedModelMap();
    private Page<SearchResult> resultsPage;
    private List<SearchResult> entries = new ArrayList<>();
    private SearchForm searchForm = new SearchForm();

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        controller = new SearchController(searchService);
        SearchResult entry = new SearchResult("", "", "", "", "", "", "", "original search term");
        entries.add(entry);
        resultsPage = new PageImpl<>(entries);
        given(searchService.search(anyString(), (Pageable) anyObject(), anyList())).willReturn(
                new SearchResults(resultsPage, Collections.<SearchFacet> emptyList()));
    }

    @Test
    public void search_providesQueryInModel() {
        searchForm.setQ("searchTerm");
        controller.search(searchForm, 1, model);
        assertThat((SearchForm) model.get("searchForm"), equalTo(searchForm));
    }

    @Test
    public void search_providesPaginationInfoInModel() {
        searchForm.setQ("searchTerm");
        controller.search(searchForm, 1, model);
        assertThat(model.get("paginationInfo"), is(notNullValue()));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void search_providesResultsInModel() {
        searchForm.setQ("searchTerm");
        controller.search(searchForm, 1, model);
        assertThat((List<SearchResult>) model.get("results"), equalTo(entries));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void search_providesAllResultsForBlankQuery() {
        searchForm.setQ("");
        controller.search(searchForm, 1, model);
        assertThat((List<SearchResult>) model.get("results"), equalTo(entries));
    }

}
