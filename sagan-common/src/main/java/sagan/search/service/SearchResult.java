package sagan.search.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResult {
    private final String type;
    private final String title;
    private final String subTitle;
    private final String summary;
    private final String path;
    private final String id;
    private final String highlight;
    private final String originalSearchTerm;

    SearchResult() {
        this(null, null, null, null, null, null, null, null);
    }

    public SearchResult(String id, String title, String subTitle, String summary, String path, String type,
                        String highlight, String originalSearchTerm) {
        this.title = title;
        this.subTitle = subTitle;
        this.summary = summary;
        this.path = path;
        this.id = id;
        this.type = type;
        this.highlight = highlight;
        this.originalSearchTerm = originalSearchTerm;
    }

    public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public String getSummary() {
        return summary;
    }

    public String getPath() {
        return path;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getHighlight() {
        return highlight;
    }

    public String getOriginalSearchTerm() {
        return originalSearchTerm;
    }

    public String getDisplayText() {
        if (getType().equals("apiDoc") && getTitle().startsWith(getOriginalSearchTerm())) {
            return getSummary();
        } else {
            return getHighlight() != null ? getHighlight() : getSummary();
        }
    }
}
