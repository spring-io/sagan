package sagan.search.service.web;

import sagan.search.service.SearchQuery;
import sagan.search.service.SearchResults;
import sagan.search.service.SearchService;
import sagan.search.types.SearchEntry;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UrlPathHelper;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@RestController
public class SearchServiceController {

    private final SearchService service;

    private UrlPathHelper helper = new UrlPathHelper();

    @Autowired
    public SearchServiceController(SearchService service) {
        this.service = service;
        this.helper.setAlwaysUseFullPath(true);
    }

    @PostMapping("/index")
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@RequestBody GenericSearchEntry entry) {
        service.saveToIndex(entry);
    }

    @PostMapping("/search")
    @ResponseStatus(HttpStatus.CREATED)
    public SearchResults search(@RequestBody SearchQuery query, Pageable pageable) {
        return service.search(query.getTerm(), pageable, query.getFilter());
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class GenericSearchEntry extends SearchEntry {

        Map<String, Object> unknown = new HashMap<>();

        private String type;

        GenericSearchEntry() {
        }

        public GenericSearchEntry(String type, String path) {
            this.type = type;
            setPath(path);
        }

        @Override
        public String getType() {
            return type;
        }

        @JsonAnyGetter
        public Map<String, Object> getUnknownFields() {
            return unknown;
        }

        @JsonAnySetter
        public void setUnknownField(String name, Object value) {
            unknown.put(name, value);
        }

    }
}
