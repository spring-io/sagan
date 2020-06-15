package sagan.site.guides;

import sagan.SiteProperties;
import sagan.site.renderer.SaganRendererClient;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@RestClientTest({GettingStartedGuides.class, SaganRendererClient.class, SiteProperties.class})
@TestPropertySource(properties = "sagan.site.renderer.service-url=https://example.com/")
public class GuidesRepositoryTest {

	@Autowired
	private GettingStartedGuides repository;

	@Autowired
	private MockRestServiceServer server;

	@Test
	public void findAllShouldReturnOnlyGettingStartedGuides() {
		this.server.expect(requestTo("https://example.com/"))
				.andRespond(withSuccess(getClassPathResource("root.json"), MediaTypes.HAL_JSON));
		this.server.expect(requestTo("/guides/"))
				.andRespond(withSuccess(getClassPathResource("guides.json"), MediaTypes.HAL_JSON));

		assertThat(this.repository.findAll()).extracting("name").containsExactlyInAnyOrder("rest-service", "messaging-redis");

	}

	private ClassPathResource getClassPathResource(String path) {
		return new ClassPathResource(path, SaganRendererClient.class);
	}

}