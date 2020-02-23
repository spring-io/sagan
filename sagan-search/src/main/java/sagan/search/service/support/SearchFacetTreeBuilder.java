package sagan.search.service.support;

import sagan.search.service.SearchFacet;

import java.util.List;
import java.util.Stack;

class SearchFacetTreeBuilder {
    private SearchFacet root = new SearchFacet("", "", 0);
    private Stack<SearchFacet> previousFacets = new Stack<>();

    public SearchFacetTreeBuilder() {
        previousFacets.push(root);
    }

    public void addTerm(String term, int count) {
        String path = "";
        String name = term;

        int pathSeparatorIndex = term.lastIndexOf("/");
        if (pathSeparatorIndex != -1) {
            path = term.substring(0, pathSeparatorIndex);
            name = term.substring(pathSeparatorIndex + 1);
        }

        while (!previousFacets.peek().getFullPath().equals("") && !previousFacets.peek().getFullPath().equals(path)) {
            previousFacets.pop();
        }

        SearchFacet facet = new SearchFacet(path, name, count);
        previousFacets.peek().getFacets().add(facet);
        previousFacets.push(facet);
    }

    public List<SearchFacet> build() {
        return root.getFacets();
    }
}
