package sagan.site.guides;

import sagan.SiteProperties;
import sagan.site.guides.GuidesStubRunnerTests.TestConfiguration;
import sagan.site.renderer.SaganRendererClient;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.web.WebClientAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.contract.wiremock.WireMockConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { GettingStartedGuides.class, SaganRendererClient.class, SiteProperties.class,
        WebClientAutoConfiguration.class, TestConfiguration.class },
                webEnvironment = WebEnvironment.NONE,
                properties = "spring.main.sources=")
@AutoConfigureWireMock(stubs = "file:../sagan-renderer/build/stubs/**/*.json", port = 8081)
public class GuidesStubRunnerTests {

    @Autowired
    private GettingStartedGuides repository;

    @Test
    public void findAllShouldReturnOnlyGettingStartedGuides() throws Exception {
        assertThat(this.repository.findAll()).extracting("name").containsExactlyInAnyOrder("rest-service",
                "securing-web");
    }

    @SpringBootConfiguration
    public static class TestConfiguration {
        @Bean
        public WireMockConfigurationCustomizer wireMockConfigurationCustomizer() {
            // Ensure Hypermedia links are rendered with the correct port
            return config -> config.extensions(new ResponseTemplateTransformer(false));
        }
    }
}
