package sagan.search.support;

import sagan.search.service.SearchFacet;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.IsEqual.equalTo;

public class SpringFacetsBuilderTests {

    private List<SearchFacet> facets = new ArrayList<>();
    private SearchFacet guides = new SearchFacet("Guides", "Guides", 1);
    private SearchFacet blog = new SearchFacet("Blog", "Blog", 1);
    private SearchFacet projects = new SearchFacet("Projects", "Projects", 1);
    private SearchFacet api = new SearchFacet("Projects/Api", "Api", 1);
    private SearchFacet reference = new SearchFacet("Projects/Reference", "Reference", 1);
    private SearchFacet projectPage = new SearchFacet("Projects/Homepage", "Homepage", 1);
    private SearchFacet springFramework = new SearchFacet("Projects/SpringFramework", "Spring Framework", 1);

    @Before
    public void setUp() throws Exception {
        facets.add(blog);
        facets.add(guides);
        facets.add(projects);
    }

    @Test
    public void createsRootFacetWithProvidedFacets() throws Exception {
        SearchFacet root = new SpringFacetsBuilder(facets).build();

        assertThat(root.getFacets(), equalTo(facets));
        assertThat(root.getName(), equalTo(""));
        assertThat(root.getCount(), equalTo(0));
    }

    @Test
    public void addsAnEmptyBlogFacetIfNotInTheOriginalFacets() throws Exception {
        facets.remove(blog);

        SearchFacet rootFacet = new SpringFacetsBuilder(facets).build();

        SearchFacet emptyBlog = new SearchFacet("Blog", "Blog", 0);

        assertThat(rootFacet.getFacets(), contains(emptyBlog, guides, projects));
    }

    @Test
    public void addsAnEmptyGuidesFacetIfNotInTheOriginalFacets() throws Exception {
        facets.remove(guides);

        SearchFacet rootFacet = new SpringFacetsBuilder(facets).build();

        SearchFacet emptyGuides = new SearchFacet("Guides", "Guides", 0);

        assertThat(rootFacet.getFacets(), contains(blog, emptyGuides, projects));
    }

    @Test
    public void addsAnEmptyProjectsFacetIfNotInTheOriginalFacets() throws Exception {
        facets.remove(projects);

        SearchFacet rootFacet = new SpringFacetsBuilder(facets).build();

        SearchFacet emptyProjects = new SearchFacet("Projects", "Projects", 0);

        assertThat(rootFacet.getFacets(), contains(blog, guides, emptyProjects));
    }

    @Test
    public void movesProjectPageToBeAHeaderFacetUnderProjects() throws Exception {
        projects.getFacets().add(projectPage);
        projects.getFacets().add(springFramework);

        SearchFacet rootFacet = new SpringFacetsBuilder(facets).build();

        SearchFacet projectFacet = rootFacet.getFacets().get(2);
        assertThat(projectFacet.getHeaderFacets(), contains(projectPage));
        assertThat(projectFacet.getFacets(), contains(springFramework));
    }

    @Test
    public void movesApiToBeAHeaderFacetUnderProjects() throws Exception {
        projects.getFacets().add(api);
        projects.getFacets().add(springFramework);

        SearchFacet rootFacet = new SpringFacetsBuilder(facets).build();

        SearchFacet projectFacet = rootFacet.getFacets().get(2);
        assertThat(projectFacet.getHeaderFacets(), contains(api));
        assertThat(projectFacet.getFacets(), contains(springFramework));
    }


    @Test
    public void movesReferenceToBeAHeaderFacetUnderProjects() throws Exception {
        facets.add(projects);
        projects.getFacets().add(reference);
        projects.getFacets().add(springFramework);

        SearchFacet rootFacet = new SpringFacetsBuilder(facets).build();

        SearchFacet projectFacet = rootFacet.getFacets().get(2);
        assertThat(projectFacet.getHeaderFacets(), contains(reference));
        assertThat(projectFacet.getFacets(), contains(springFramework));
    }

}
