package sagan.search.elastic;

import sagan.search.service.support.ElasticSearchService;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
class InMemoryElasticSearchConfig {

    private static final Logger log = LoggerFactory.getLogger(InMemoryElasticSearchConfig.class);

    @Autowired
    private ElasticSearchService searchService;

    private Client client;

    private Node node;

    private CountDownLatch latch = new CountDownLatch(1);

    @PostConstruct
    public void configureSearchService() throws InterruptedException {
        searchService.setUseRefresh(true);
        new Thread(this::start).start();
        latch.await(5000L, TimeUnit.MILLISECONDS);
    }

    private void start() {
        log.info("Starting Elastic Search");
        NodeBuilder nodeBuilder = NodeBuilder.nodeBuilder().local(true);
        nodeBuilder.getSettings().put("network.host", "127.0.0.1");
        node = nodeBuilder.node();
        client = node.client();
        log.info("Started Elastic Search");
        latch.countDown();
    }

    @PreDestroy
    public void shutDownElasticSearch() throws Exception {
        if (node != null) {
            try {
                client.close();
                node.close();
            } catch (Throwable e) {
                log.warn("Failed to shutdown Elastic Search");
            }
        }
    }
}
