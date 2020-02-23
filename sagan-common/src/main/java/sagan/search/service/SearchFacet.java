package sagan.search.service;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchFacet {
    private String path;
    private String name;
    private int count;
    private List<SearchFacet> facets;
    private List<SearchFacet> headerFacets = new ArrayList<>();
    
    SearchFacet() {
        this(null, null, 0);
    }

    public SearchFacet(String path, String name, int count) {
        this(path, name, count, new ArrayList<>());
    }

    public SearchFacet(String path, String name, int count, List<SearchFacet> facets) {
        this.path = path;
        this.name = name;
        this.count = count;
        this.facets = facets;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public String getFullPath() {
        if (path.equals("")) {
            return name;
        }
        return path + "/" + name;
    }

    public int getCount() {
        return count;
    }

    public List<SearchFacet> getFacets() {
        return facets;
    }

    public boolean hasFacets() {
        return facets.size() > 0;
    }

    public String getLinkText() {
        return String.format("%s (%d)", name, count);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        SearchFacet that = (SearchFacet) o;

        if (count != that.count)
            return false;
        if (!facets.equals(that.facets))
            return false;
        if (!name.equals(that.name))
            return false;
        if (!path.equals(that.path))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = path.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + count;
        result = 31 * result + facets.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "SearchFacet{" + "path='" + path + '\'' + ", name='" + name + '\'' + ", count=" + count + ", facets="
                + facets + '}';
    }

    public List<SearchFacet> getHeaderFacets() {
        return headerFacets;
    }

    public void addHeaderFacet(SearchFacet headerFacet) {
        headerFacets.add(headerFacet);
    }

    public boolean hasHeaderFacets() {
        return !headerFacets.isEmpty();
    }
}
