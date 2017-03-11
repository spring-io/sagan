package sagan.projects;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;

/**
 * @author Josh Long
 */
public class ProjectLabelsTests {

    @Test
    public void testInitialization() throws Exception {

        Project project = new Project("spring-boot", "Spring Boot", "http://some-repo.com",
                "http://some-url.com", new ArrayList<>(), false, "active", "", new HashSet<>());
        assertThat(project.getProjectLabels().size(), equalTo(0));

        ProjectLabel projectLabel = new ProjectLabel("bootiful");
        project.addProjectLabel(projectLabel);
        assertThat(projectLabel.getProjects(), contains(project));
        projectLabel = new ProjectLabel("cloudy");
        project.addProjectLabel(projectLabel);
        assertThat(project.getProjectLabels().size(), equalTo(2));
    }
}
