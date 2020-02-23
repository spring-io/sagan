package sagan.search.support;

import sagan.search.service.SearchResult;

public class SearchResultBuilder {

    private String title = "A random title";
    private String summary = "A random post summary";
    private String path = "/some/path";
    private String type = "site";
    private String highlight = "highlight";
    private String id = "id";
    private String originalSearchTerm = "term";
    private String subtitle = "subtitle";

    private SearchResultBuilder() {
    }

    public static SearchResultBuilder entry() {
        return new SearchResultBuilder();
    }

    public SearchResultBuilder title(String title) {
        this.title = title;
        return this;
    }

    public SearchResultBuilder summary(String summary) {
        this.summary = summary;
        return this;
    }

    public SearchResultBuilder path(String path) {
        this.path = path;
        return this;
    }

    public SearchResult build() {
        SearchResult searchResult =
                new SearchResult(id, title, subtitle, summary, path, type,
                        highlight, originalSearchTerm);
        return searchResult;
    }

    public SearchResultBuilder id(String id) {
        this.id = id;
        return this;
    }

    public SearchResultBuilder highlight(String highlight) {
        this.highlight = highlight;
        return this;
    }

    public SearchResultBuilder originalSearchTerm(String originalSearchTerm) {
        this.originalSearchTerm = originalSearchTerm;
        return this;
    }

    public SearchResultBuilder type(String type) {
        this.type = type;
        return this;
    }

    public SearchResultBuilder subtitle(String subtitle) {
        this.subtitle = subtitle;
        return this;
    }
}
