package sagan.search.service;

import sagan.SaganApplication;
import sagan.search.service.health.ElasticsearchHealthIndicator;
import sagan.search.service.support.ElasticSearchService;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;

import io.searchbox.client.JestClient;

/**
 * The entry point for the Sagan search application.
 */
@SpringBootApplication
public class SearchApplication {


    public static void main(String[] args) {
        new SaganApplication(SearchApplication.class).run(args);
    }

    @Bean
    @ConditionalOnBean(ElasticSearchService.class)
    public ElasticsearchHealthIndicator elasticsearchHealthIndicator(JestClient jestClient, ElasticSearchService searchService) {
        return new ElasticsearchHealthIndicator(jestClient, searchService.getIndexName());
    }

}
