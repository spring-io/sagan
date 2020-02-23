package sagan.search.service.web;

import sagan.search.service.SearchService;
import sagan.search.service.web.SearchServiceController.GenericSearchEntry;

import java.util.Date;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.main.sources=")
@RunWith(SpringRunner.class)
@JsonTest
public class JsonMarshallingTests {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private SearchService service;

    @Test
    public void testMarshal() throws Exception {
        GenericSearchEntry results = new GenericSearchEntry("foo", "/foo");
        results.setSummary("Lorem ipsum");
        String json = mapper.writeValueAsString(results);
        // System.err.println(json);
        GenericSearchEntry restored = mapper.readValue(json, GenericSearchEntry.class);
        assertThat(restored.getPath()).isEqualTo("/foo");
        assertThat(restored.getSummary()).isEqualTo("Lorem ipsum");
        assertThat(restored.getType()).isEqualTo("foo");
    }

    @Test
    public void testMarshalUnknown() throws Exception {
        GenericSearchEntry results = new GenericSearchEntry("foo", "/foo");
        results.setUnknownField("publishAt", new Date());
        results.setSummary("Lorem ipsum");
        String json = mapper.writeValueAsString(results);
        // System.err.println(json);
        GenericSearchEntry restored = mapper.readValue(json, GenericSearchEntry.class);
        assertThat(restored.getPath()).isEqualTo("/foo");
        assertThat(restored.getSummary()).isEqualTo("Lorem ipsum");
        assertThat(restored.getType()).isEqualTo("foo");
        assertThat(restored.getUnknownFields()).containsKey("publishAt");
        @SuppressWarnings("unchecked")
        Map<String,Object> map = mapper.readValue(json, Map.class);
        assertThat(map).containsKey("publishAt");
        assertThat(map).containsKey("path");
    }

    @SpringBootApplication
    @Import(SearchServiceController.class)
    public static class Nested {}

}
