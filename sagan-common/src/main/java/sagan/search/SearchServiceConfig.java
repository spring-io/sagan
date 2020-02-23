package sagan.search;

import sagan.SaganProfiles;
import sagan.search.remote.RemoteSearchService;
import sagan.search.support.JestSearchService;
import sagan.search.support.SearchResultParser;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.searchbox.client.JestClient;

import com.google.gson.Gson;


@Configuration
@Profile(SaganProfiles.JEST)
class JestSearchServiceConfig {

    @Bean
    public SearchResultParser searchResultParser() {
        return new SearchResultParser();
    }

    @Bean
    public JestSearchService searchService(JestClient jestClient, SearchResultParser searchResultParser, Gson gson) {
        return new JestSearchService(jestClient, searchResultParser, gson);
    }
}

@Configuration
@Profile("!" + SaganProfiles.JEST)
class RemoteSearchServiceConfig {

    @Bean
    public RemoteSearchService searchService(RestTemplateBuilder builder) {
        return new RemoteSearchService(builder.build());
    }
}
