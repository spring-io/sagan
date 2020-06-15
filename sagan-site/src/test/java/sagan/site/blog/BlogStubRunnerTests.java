package sagan.site.blog;

import sagan.SiteProperties;
import sagan.blog.PostFormat;
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
@SpringBootTest(classes = { SaganRendererClient.class, SiteProperties.class, PostContentRenderer.class,
        WebClientAutoConfiguration.class, TestConfiguration.class },
                webEnvironment = WebEnvironment.NONE,
                properties = "spring.main.sources=")
@AutoConfigureWireMock(stubs = "file:../sagan-renderer/build/stubs/**/*.json", port = 8081)
public class BlogStubRunnerTests {

    @Autowired
    private PostContentRenderer renderer;

    @Test
    public void shouldRenderMarkdown() {
        String result = this.renderer.render("## What is Lorem Ipsum?\n\n_Lorem ipsum_ dolor sit amet", PostFormat.MARKDOWN);
        assertThat(result).contains("<h2>");
        assertThat(result).contains("<em>Lorem");
    }

    @Test
    public void shouldRenderAsciidoctor() {
        String result = this.renderer.render("== What is Lorem Ipsum?\n\n_Lorem ipsum_ dolor sit amet", PostFormat.ASCIIDOC);
        assertThat(result).contains("<h2");
        assertThat(result).contains("<em>Lorem");
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
