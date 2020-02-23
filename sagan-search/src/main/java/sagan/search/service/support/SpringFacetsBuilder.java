package sagan.search.service.support;

import sagan.search.service.SearchFacet;

import java.util.ArrayList;
import java.util.List;

class SpringFacetsBuilder {

    private final List<SearchFacet> facets;

    public SpringFacetsBuilder(List<SearchFacet> facets) {
        this.facets = new ArrayList<>(facets);
    }

    public SearchFacet build() {
        ensureFacetExists(0, "Blog");
        ensureFacetExists(1, "Guides");
        SearchFacet projects = ensureFacetExists(2, "Projects");
        moveFacetToHeader(projects, "Api");
        moveFacetToHeader(projects, "Homepage");
        moveFacetToHeader(projects, "Reference");
        return new SearchFacet("", "", 0, facets);
    }

    private SearchFacet ensureFacetExists(int position, String facetName) {
        SearchFacet facet = getFacetWithName(facets, facetName);
        if (facet == null) {
            facet = new SearchFacet(facetName, facetName, 0);
            facets.add(position, facet);
        }
        return facet;
    }

    private SearchFacet getFacetWithName(List<SearchFacet> facets, String facetName) {
        for (SearchFacet facet : facets) {
            if (facet.getName().equalsIgnoreCase(facetName)) {
                return facet;
            }
        }
        return null;
    }

    private void moveFacetToHeader(SearchFacet project, String facetName) {
        SearchFacet facet = getFacetWithName(project.getFacets(), facetName);
        if (facet != null) {
            project.getFacets().remove(facet);
            project.addHeaderFacet(facet);
        }
    }
}
