package sagan.search.service.support;

import sagan.search.service.SearchFacet;
import sagan.search.service.support.SearchFacetTreeBuilder;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class SearchFacetTreeBuilderTests {

    private SearchFacetTreeBuilder builder;
    private List<SearchFacet> facets;

    @Before
    public void setUp() throws Exception {
        builder = new SearchFacetTreeBuilder();
        builder.addTerm("Guides", 2);
        builder.addTerm("Guides/GettingStarted", 1);
        builder.addTerm("Guides/GettingStarted/SomethingElse", 1);
        builder.addTerm("Guides/GettingStarted/SomeOther", 1);
        builder.addTerm("Guides/Tutorials", 1);
        builder.addTerm("Guides/Tutorials/123", 1);
        builder.addTerm("Blog", 5);
        facets = builder.build();
    }

    @Test
    public void hasTopLevelFacets() {
        assertThat(facets.size(), equalTo(2));
    }

    @Test
    public void hasTopLevelName() {
        assertThat(facets.get(0).getName(), equalTo("Guides"));
    }

    @Test
    public void hasTopLevelCount() {
        assertThat(facets.get(0).getCount(), equalTo(2));
    }

    @Test
    public void hasTwoNestedFacets() {
        assertThat(facets.get(0).getFacets().size(), equalTo(2));
    }

    @Test
    public void hasNestedFacetName() {
        assertThat(facets.get(0).getFacets().get(0).getName(), equalTo("GettingStarted"));
    }

    @Test
    public void hasNestedFacetCount() {
        assertThat(facets.get(0).getFacets().get(0).getCount(), equalTo(1));
    }

    @Test
    public void hasDoubleNestedFacets() {
        assertThat(facets.get(0).getFacets().get(0).getFacets().size(), equalTo(2));
    }

    @Test
    public void hasDoubleNestedFacetName() {
        assertThat(facets.get(0).getFacets().get(0).getFacets().get(0).getName(), equalTo("SomethingElse"));
    }

    @Test
    public void hasDoubleNestedFacetCount() {
        assertThat(facets.get(0).getFacets().get(0).getFacets().get(0).getCount(), equalTo(1));
    }
}
