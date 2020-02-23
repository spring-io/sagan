package sagan.search.service;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.main.sources=")
@RunWith(SpringRunner.class)
@JsonTest
public class JsonMarshallingTests {

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void testMarshal() throws Exception {
        Page<SearchResult> page = new PageImpl<>(Arrays.asList(new SearchResult("0000", "foo", "bar", "", "/foo", "text", "", "foo")), new PageRequest(3, 10), 41);
        SearchResults results = new SearchResults(page, Collections.emptyList());
        String json = mapper.writeValueAsString(results);
        // System.err.println(json);
        SearchResults restored = mapper.readValue(json, SearchResults.class);
        assertThat(restored.getPage().getSize()).isEqualTo(10);
        assertThat(restored.getPage().getTotalElements()).isEqualTo(41);
        assertThat(restored.getPage().getContent()).hasSize(1);
        assertThat(restored.getPage().getContent().get(0).getPath()).isEqualTo("/foo");
    }

    @SpringBootApplication
    public static class Nested {}

}
