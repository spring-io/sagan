package sagan.projects.support;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import saganx.AbstractIntegrationTests;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectsMetadataApiTests extends AbstractIntegrationTests {

    @Test
    public void projectMetadata_ids() throws Exception {
        ObjectMapper om = new ObjectMapper();
        mockMvc.perform(
                MockMvcRequestBuilders.get("/project_metadata/projects")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(mvcResult -> {
                    String string = mvcResult.getResponse().getContentAsString();
                    List<String> content = om.readValue(string, new TypeReference<List<String>>() {});
                    MatcherAssert.assertThat( "this endpoint should return ids for all the projects",
                            content.contains("spring-boot"));
                });
    }

    @Test
    public void projectMetadata_respondsWithJavascript() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders
                                .get("/project_metadata/spring-framework?callback=a_function_name"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/javascript"));
    }

    @Test
    public void projectMetadata_respondsWithCallback_andData() throws Exception {
        List<Object> releases = getAndCheckProjectReleases("spring-framework", "Spring Framework");

        checkCurrentRelease(releases);
        checkMilestone(releases);
    }

    @Test
    public void projectMetadata_WithSnapshot() throws Exception {
        List<Object> releases = getAndCheckProjectReleases("spring-security-kerberos", "Spring Security Kerberos");

        checkSnapshot(releases);
    }

    public List<Object> getAndCheckProjectReleases(String projectId, String expectedProjectName) throws Exception {
        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.get("/project_metadata/" + projectId
                        + "?callback=a_function_name")).andReturn();

        String content = result.getResponse().getContentAsString();

        String functionNameRegex = "^([^(]*)\\((.*)\\);$";
        Matcher matcher = Pattern.compile(functionNameRegex).matcher(content);
        if (matcher.find()) {
            assertThat(matcher.group(1), equalTo("/**/a_function_name"));

            Map<String, Object> projectMetadata = new JacksonJsonParser().parseMap(matcher.group(2));

            assertThat((String) projectMetadata.get("name"), equalTo(expectedProjectName));
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>) projectMetadata.get("projectReleases");
            return list;
        } else {
            fail(String.format("no match found: %s", content));
            return null;
        }
    }

    private void checkCurrentRelease(List<Object> releases) {
        @SuppressWarnings("unchecked")
        Map<String, Object> release = (Map<String, Object>) releases.get(0);
        assertThat((String) release.get("version"), equalTo("4.0.1.BUILD-SNAPSHOT"));
        assertThat(
                (String) release.get("refDocUrl"),
                equalTo("http://docs.spring.io/spring/docs/4.0.1.BUILD-SNAPSHOT/spring-framework-reference/htmlsingle/"));
        assertThat((String) release.get("apiDocUrl"),
                equalTo("http://docs.spring.io/spring/docs/4.0.1.BUILD-SNAPSHOT/javadoc-api/"));
        assertThat((Boolean) release.get("preRelease"), equalTo(false));
        assertThat((Boolean) release.get("current"), equalTo(false));
        assertThat((Boolean) release.get("generalAvailability"), equalTo(false));
        assertThat((Boolean) release.get("snapshot"), equalTo(true));

        Map<String, Object> repository = getRepository(release);
        assertThat(repository, notNullValue());
    }

    private void checkMilestone(List<Object> releases) {
        @SuppressWarnings("unchecked")
        Map<String, Object> release = (Map<String, Object>) releases.get(1);
        assertThat((String) release.get("version"), equalTo("4.0.0.RELEASE"));
        assertThat((String) release.get("refDocUrl"),
                equalTo("http://docs.spring.io/spring/docs/4.0.0.RELEASE/spring-framework-reference/htmlsingle/"));
        assertThat((String) release.get("apiDocUrl"),
                equalTo("http://docs.spring.io/spring/docs/4.0.0.RELEASE/javadoc-api/"));
        assertThat((Boolean) release.get("preRelease"), equalTo(false));
        assertThat((Boolean) release.get("current"), equalTo(true));
        assertThat((Boolean) release.get("generalAvailability"), equalTo(true));
        assertThat((Boolean) release.get("snapshot"), equalTo(false));
    }

    private void checkSnapshot(List<Object> releases) {
        @SuppressWarnings("unchecked")
        Map<String, Object> release = (Map<String, Object>) releases.get(0);
        assertThat((String) release.get("version"), equalTo("1.0.0.CI-SNAPSHOT"));
        assertThat((String) release.get("refDocUrl"),
                equalTo("http://docs.spring.io/spring-security-kerberos/docs/1.0.0.CI-SNAPSHOT/reference/html"));
        assertThat((String) release.get("apiDocUrl"),
                equalTo("http://docs.spring.io/spring-security-kerberos/docs/1.0.0.CI-SNAPSHOT/api/"));
        assertThat((Boolean) release.get("preRelease"), equalTo(false));
        assertThat((Boolean) release.get("current"), equalTo(false));
        assertThat((Boolean) release.get("generalAvailability"), equalTo(false));
        assertThat((Boolean) release.get("snapshot"), equalTo(true));

        Map<String, Object> repository = getRepository(release);

        assertThat((String) repository.get("id"), equalTo("spring-snapshots"));
        assertThat((String) repository.get("name"), equalTo("Spring Snapshots"));
        assertThat((String) repository.get("url"), equalTo("https://repo.spring.io/libs-snapshot"));
        assertThat((Boolean) repository.get("snapshotsEnabled"), equalTo(true));
    }

    private Map<String, Object> getRepository(Map<String, Object> release) {
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) release.get("repository");
        return map;
    }

}
