package sagan.search.elastic;

import sagan.SaganProfiles;
import sagan.search.service.SearchFacet;
import sagan.search.service.SearchResult;
import sagan.search.service.SearchResults;
import sagan.search.service.support.ElasticSearchService;
import sagan.search.support.SearchEntryBuilder;
import sagan.search.support.SearchIndexSetup;
import sagan.search.types.SearchEntry;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import io.searchbox.client.JestClient;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { InMemoryElasticSearchConfig.class })
@ActiveProfiles(profiles = { SaganProfiles.STANDALONE, SaganProfiles.JEST })
public class SearchFacetsIntegrationTests {

    private final Pageable pageable = new PageRequest(0, 10);

    @Autowired
    private ElasticSearchService searchService;

    @Autowired
    private JestClient jestClient;

    private SearchEntry gettingStarted = SearchEntryBuilder.entry()
            .path("http://example.com/gettingStarted/some")
            .title("a title")
            .facetPath("Guides")
            .facetPath("Guides/Getting Started").guide();

    private SearchEntry tutorial = SearchEntryBuilder.entry()
            .path("http://example.com/tutorial")
            .title("a title")
            .facetPath("Guides")
            .facetPath("Guides/Tutorials").guide();

    private SearchEntry blog = SearchEntryBuilder.entry()
            .path("http://example.com/blog")
            .title("a title")
            .facetPath("Blog")
            .facetPath("Blog/Engineering").blog();

    private SearchEntry springFrameworkApiDoc = SearchEntryBuilder.entry()
            .path("http://example.com/projects/springFramework/apiDoc")
            .title("a title")
            .facetPath("Projects")
            .facetPath("Projects/Api")
            .facetPath("Projects/SpringFramework")
            .facetPath("Projects/SpringFramework/3.4.5.RELEASE").api();

    private SearchEntry springFrameworkRefDoc = SearchEntryBuilder.entry()
            .path("http://example.com/projects/springFramework/refDoc")
            .title("a title")
            .facetPath("Projects")
            .facetPath("Projects/Reference")
            .facetPath("Projects/SpringFramework")
            .facetPath("Projects/SpringFramework/3.4.5.RELEASE").reference();

    private SearchEntry springSecurityApiDoc = SearchEntryBuilder.entry()
            .path("http://example.com/projects/spring-security/apiDoc")
            .title("a title")
            .facetPath("Projects")
            .facetPath("Projects/Api")
            .facetPath("Projects/SpringSecurity")
            .facetPath("Projects/SpringSecurity/1.2.3.RELEASE").api();

    private SearchEntry springSecurityRefDoc = SearchEntryBuilder.entry()
            .path("http://example.com/projects/spring-security/refDoc")
            .title("a title")
            .facetPath("Projects")
            .facetPath("Projects/Reference")
            .facetPath("Projects/SpringSecurity")
            .facetPath("Projects/SpringSecurity/1.2.3.RELEASE").reference();

    @Value("${elasticsearch.client.index}")
    private String index;
    private SearchIndexSetup searchIndexSetup;

    @Before
    public void setUp() throws Exception {
        searchIndexSetup = new SearchIndexSetup(jestClient, index);
        searchIndexSetup.deleteIndex();
        searchIndexSetup.createIndex();
    }

    @After
    public void tearDown() throws Exception {
        searchIndexSetup.deleteIndex();
        searchIndexSetup.createIndex();
    }

    @Test
    public void searchReturnsFacetInformation() throws ParseException {
        searchService.saveToIndex(gettingStarted);
        searchService.saveToIndex(blog);

        List<String> emptyFacetFilters = Collections.emptyList();
        SearchResults searchResults = searchService.search("title", pageable, emptyFacetFilters);

        List<SearchResult> content = searchResults.getPage().getContent();
        assertThat(content.size(), equalTo(2));
        assertThat(searchResults.getFacets().size(), equalTo(2));
        assertThat(searchResults.getFacets().get(0).getName(), equalTo("Blog"));
        assertThat(searchResults.getFacets().get(1).getName(), equalTo("Guides"));
    }

    @Test
    public void filterByFacetsReturnsFilteredContent() throws ParseException {
        searchService.saveToIndex(tutorial);
        searchService.saveToIndex(gettingStarted);
        searchService.saveToIndex(blog);

        List<String> facetFilter = Arrays.asList("Guides");
        SearchResults searchResults = searchService.search("title", pageable, facetFilter);

        List<SearchResult> content = searchResults.getPage().getContent();
        assertThat(content.size(), equalTo(2));
    }

    @Test
    public void filterByMultipleFacetsAllowsMatchesFromUnrelatedFacets() throws ParseException {
        searchService.saveToIndex(tutorial);
        searchService.saveToIndex(gettingStarted);
        searchService.saveToIndex(blog);
        searchService.saveToIndex(springSecurityApiDoc);
        searchService.saveToIndex(springSecurityRefDoc);

        List<String> facetPathFilters = new ArrayList<>();
        facetPathFilters.add("Guides/Tutorials");
        facetPathFilters.add("Blog");
        facetPathFilters.add("Projects/SpringSecurity");

        SearchResults searchResults = searchService.search("title", pageable, facetPathFilters);
        List<SearchResult> results = searchResults.getPage().getContent();

        assertThatResultsContains(results, tutorial, blog, springSecurityApiDoc, springSecurityRefDoc);
    }

    private void assertThatResultsContains(List<SearchResult> results, SearchEntry... entries) {
        List<String> resultPaths = results.stream()
                .map(SearchResult::getPath).collect(toList());

        List<String> expectedPaths = Arrays.asList(entries).stream()
                .map(SearchEntry::getPath).collect(toList());

        assertThat(resultPaths, containsInAnyOrder(expectedPaths.toArray()));
    }

    @Test
    public void filterByMultipleFacetsProjectAndApi_restrictsProjectsToApiAndIncludesAllOtherFacets()
            throws ParseException {
        searchService.saveToIndex(tutorial);
        searchService.saveToIndex(blog);
        searchService.saveToIndex(springFrameworkApiDoc);
        searchService.saveToIndex(springFrameworkRefDoc);
        searchService.saveToIndex(springSecurityApiDoc);
        searchService.saveToIndex(springSecurityRefDoc);

        List<String> facetPathFilters = new ArrayList<>();
        facetPathFilters.add("Blog");
        facetPathFilters.add("Projects/Api");
        facetPathFilters.add("Projects/SpringSecurity/1.2.3.RELEASE");

        SearchResults searchResults = searchService.search("title", pageable, facetPathFilters);
        List<SearchResult> results = searchResults.getPage().getContent();

        assertThatResultsContains(results, blog, springSecurityApiDoc);
    }

    @Test
    public void filterByMultipleFacetsProjectAndReference_restrictsProjectsToReferenceAndIncludesAllOtherFacets()
            throws ParseException {
        searchService.saveToIndex(tutorial);
        searchService.saveToIndex(blog);
        searchService.saveToIndex(springFrameworkApiDoc);
        searchService.saveToIndex(springFrameworkRefDoc);
        searchService.saveToIndex(springSecurityApiDoc);
        searchService.saveToIndex(springSecurityRefDoc);

        List<String> facetPathFilters = new ArrayList<>();
        facetPathFilters.add("Blog");
        facetPathFilters.add("Projects/Reference");
        facetPathFilters.add("Projects/SpringFramework");

        SearchResults searchResults = searchService.search("title", pageable, facetPathFilters);
        List<SearchResult> results = searchResults.getPage().getContent();

        assertThatResultsContains(results, blog, springFrameworkRefDoc);
    }

    @Test
    public void filterByMultipleProjectVersionsFacets() throws ParseException {
        searchService.saveToIndex(tutorial);
        searchService.saveToIndex(blog);
        searchService.saveToIndex(springFrameworkApiDoc);
        searchService.saveToIndex(springFrameworkRefDoc);
        searchService.saveToIndex(springSecurityApiDoc);
        searchService.saveToIndex(springSecurityRefDoc);

        List<String> facetPathFilters = new ArrayList<>();
        facetPathFilters.add("Blog");
        facetPathFilters.add("Projects/SpringFramework/3.4.5.RELEASE");
        facetPathFilters.add("Projects/SpringSecurity/1.2.3.RELEASE");

        SearchResults searchResults = searchService.search("title", pageable, facetPathFilters);
        List<SearchResult> results = searchResults.getPage().getContent();

        assertThatResultsContains(results, blog, springFrameworkApiDoc, springFrameworkRefDoc, springSecurityApiDoc,
                springSecurityRefDoc);
    }

    @Test
    public void returnedFacetsAreNotAffectedByTheFacetFilters() throws ParseException {
        searchService.saveToIndex(blog);
        searchService.saveToIndex(gettingStarted);
        searchService.saveToIndex(tutorial);

        List<String> facetPathFilters = new ArrayList<>();
        facetPathFilters.add("Blog");

        SearchResults searchResults = searchService.search("title", pageable, facetPathFilters);
        List<SearchFacet> facets = searchResults.getFacets();

        assertThat(facets.size(), equalTo(2));
        assertThat(facets.get(0).getName(), equalTo("Blog"));
        assertThat(facets.get(0).getCount(), equalTo(1));
        assertThat(facets.get(1).getName(), equalTo("Guides"));
        assertThat(facets.get(1).getCount(), equalTo(2));
    }

    @Test
    public void unpublishedEntriesDoNotAppearInResultsOrFacets() throws ParseException {
        SearchEntry unpublishedPost = SearchEntryBuilder.entry()
                .path("http://example.com/blog")
                .title("a title")
                .publishAt(new Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000)))
                .facetPath("Blog")
                .facetPath("Blog/Engineering").blog();

        searchService.saveToIndex(unpublishedPost);
        searchService.saveToIndex(blog);
        searchService.saveToIndex(gettingStarted);

        SearchResults searchResults = searchService.search("title", pageable, new ArrayList<>());

        List<SearchResult> results = searchResults.getPage().getContent();
        assertThat(results.size(), equalTo(2));

        List<SearchFacet> facets = searchResults.getFacets();
        assertThat(facets.size(), equalTo(2));
        assertThat(facets.get(0).getName(), equalTo("Blog"));
        assertThat(facets.get(0).getCount(), equalTo(1));
        assertThat(facets.get(1).getName(), equalTo("Guides"));
    }

}
