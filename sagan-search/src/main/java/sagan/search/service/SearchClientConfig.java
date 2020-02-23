package sagan.search.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.Assert;

import sagan.SaganProfiles;

import java.util.Properties;


abstract class AbstractSearchClientConfig {

    private static Log logger = LogFactory.getLog(AbstractSearchClientConfig.class);

    protected abstract String getSearchClientConnectionUri();

    @Bean
    public Gson gson() {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").create();
    }

    @Bean
    public JestClient jestClient() {
        String connectionUri = getSearchClientConnectionUri();
        logger.info("**** Elastic Search endpoint: " + connectionUri);

        HttpClientConfig clientConfig = new HttpClientConfig
                .Builder(connectionUri)
                .multiThreaded(true)
                .gson(gson())
                .build();

        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(clientConfig);
        return factory.getObject();
    }

}

@Configuration
@Profile(SaganProfiles.STANDALONE)
class StandaloneSearchClientConfig extends AbstractSearchClientConfig {

    @Value("${elasticsearch.client.endpoint}")
    private String endpoint;

    @Override
    protected String getSearchClientConnectionUri() {
        return endpoint;
    }
}

@Configuration
@Profile(SaganProfiles.CLOUDFOUNDRY)
class CloudFoundrySearchClientConfig extends AbstractSearchClientConfig {

    private final static String SAGAN_SEARCH_CONNECTION_URI_KEY = "cloud.services.sagan-search.connection.uri";

    @Bean
    public Cloud cloud() {
        return new CloudFactory().getCloud();
    }


    @Override
    protected String getSearchClientConnectionUri() {
        Properties cloudProps = cloud().getCloudProperties();

        String connectionUri = cloudProps.getProperty(SAGAN_SEARCH_CONNECTION_URI_KEY);
        Assert.notNull(connectionUri, "ElasticSearch endpoint URI should not be null: "
                + SAGAN_SEARCH_CONNECTION_URI_KEY);
        return connectionUri;
    }
}
