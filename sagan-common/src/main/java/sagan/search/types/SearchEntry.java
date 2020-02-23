package sagan.search.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public abstract class SearchEntry {

    private String path;

    private String title;

    private String subTitle;

    private String summary;

    private String rawContent;

    private List<String> facetPaths = new ArrayList<>();

    public abstract String getType();

    public String getId() {
        return Base64.getEncoder().encodeToString(path.toLowerCase().getBytes());
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getRawContent() {
        return rawContent;
    }

    public void setRawContent(String rawContent) {
        this.rawContent = rawContent;
    }

    public List<String> getFacetPaths() {
        return facetPaths;
    }

    public void setFacetPaths(List<String> facetPaths) {
        this.facetPaths = facetPaths;
    }

    public void addFacetPaths(String... paths) {
        facetPaths.addAll(Arrays.asList(paths));
    }
}