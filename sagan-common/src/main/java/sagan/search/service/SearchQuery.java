package sagan.search.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class SearchQuery {

    private String term;

    private List<String> filter = new ArrayList<>();

    public SearchQuery() {
    }

    public SearchQuery(String term) {
        this.term = term;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public List<String> getFilter() {
        return filter;
    }

    public void setFilter(List<String> filter) {
        this.filter.addAll(filter);
    }

    public void addFilter(String... filter) {
        this.filter.addAll(Arrays.asList(filter));
    }
}
