package sagan.search.remote;

import sagan.search.service.SearchQuery;
import sagan.search.service.SearchResults;
import sagan.search.service.SearchService;
import sagan.search.types.SearchEntry;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class RemoteSearchService implements SearchService {

    @Value("${search.url:http://localhost:8082}")
    private String url;

    private final RestTemplate rest;

    public RemoteSearchService(RestTemplate rest) {
        this.rest = rest;
    }

    @Override
    public void saveToIndex(SearchEntry entry) {
        URI uri = UriComponentsBuilder.fromUriString(url).path("index").build().expand(
                entry.getType()).toUri();
        try {
            rest.postForEntity(uri, entry, String.class);
        } catch (RestClientException e) {
            // Ignore
        }
    }

    @Override
    public SearchResults search(String term, Pageable pageable, List<String> filter) {
        SearchQuery query = new SearchQuery(term);
        query.setFilter(filter);
        URI uri = UriComponentsBuilder.fromUriString(url).path("search").queryParam("page", pageable.getPageNumber())
                .queryParam(
                        "size", pageable.getPageSize()).build().toUri();
        try {
            return rest.exchange(RequestEntity.post(uri).body(query), SearchResults.class).getBody();
        } catch (RestClientException e) {
            return new SearchResults(new PageImpl<>(Collections.emptyList()), Collections.emptyList());
        }
    }

}
