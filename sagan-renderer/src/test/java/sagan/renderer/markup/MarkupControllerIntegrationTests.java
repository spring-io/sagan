package sagan.renderer.markup;

import static org.springframework.cloud.contract.wiremock.restdocs.WireMockRestDocs.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StreamUtils;

import sagan.renderer.github.GithubClient;

/**
 * Tests for {@link MarkupController}
 */
@RunWith(SpringRunner.class)
@AutoConfigureRestDocs(outputDir = "build/snippets")
@SpringBootTest
@AutoConfigureMockMvc
public class MarkupControllerIntegrationTests {

	private static final MediaType TEXT_ASCIIDOC = MediaType.parseMediaType("text/asciidoc");

	@Autowired
	private MockMvc mvc;

	@MockBean
	private GithubClient github;

	@Test
	public void renderMarkdown() throws Exception {
		this.mvc.perform(post("/documents").content(text(resource("lorem-ipsum.md"))).contentType(MediaType.TEXT_MARKDOWN))
				.andExpect(status().isOk())
				.andExpect(content().string(text(resource("lorem-ipsum-md.html")) + "\n<!-- rendered by Sagan Renderer Service -->"))
				.andDo(verify().contentType(MediaType.TEXT_MARKDOWN))
				.andDo(document("documents-markdown"));
	}

	@Test
	public void renderAsciidoctor() throws Exception {
		this.mvc.perform(post("/documents").content(text(resource("lorem-ipsum.adoc"))).contentType(TEXT_ASCIIDOC))
				.andExpect(status().isOk())
				.andExpect(content().string(text(resource("lorem-ipsum-adoc.html")) + "\n<!-- rendered by Sagan Renderer Service -->"))
				.andDo(verify().contentType(TEXT_ASCIIDOC))
				.andDo(document("documents-asciidoctor"));
	}
	
	private String text(Resource resource) {
		try {
			return new String(StreamUtils.copyToByteArray(resource.getInputStream()));
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private ClassPathResource resource(String path) {
		return new ClassPathResource(path, getClass());
	}

}
