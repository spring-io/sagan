package sagan.renderer;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import sagan.renderer.markup.MarkupController;

/**
 * Tests for {@link MarkupController}
 */
@RunWith(SpringRunner.class)
@AutoConfigureRestDocs(outputDir = "build/snippets")
@WebMvcTest(IndexController.class)
public class IndexControllerTests {

	@Autowired
	private MockMvc mvc;

	@Test
	public void renderIndex() throws Exception {
		this.mvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.parseMediaType("application/hal+json;charset=UTF-8")))
				.andExpect(jsonPath("$._links").exists())
				.andExpect(jsonPath("$._embedded").doesNotExist())
				.andDo(document("index"));
	}

}
