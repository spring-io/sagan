package sagan.projects.support;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sagan.projects.Project;
import sagan.projects.ProjectLabel;
import sagan.projects.ProjectRelease;
import saganx.AbstractIntegrationTests;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

public class ProjectMetadataServiceTests extends AbstractIntegrationTests {

    @Autowired
    private ProjectMetadataService service;

    @Test
    public void categoriesHaveListOfProjects() throws IOException {
        List<Project> active = service.getProjectsForCategory("active");
        assertThat(active.size(), equalTo(31));
    }

    @Test
    public void projectWithCustomSiteAndRepo() throws IOException {
        Project project = service.getProject("spring-framework");
        assertThat(project.getId(), equalTo("spring-framework"));
        assertThat(project.getName(), equalTo("Spring Framework"));
        assertThat(project.getRepoUrl(), equalTo("http://github.com/spring-projects/spring-framework"));
        assertThat(project.getSiteUrl(), equalTo("http://projects.spring.io/spring-framework"));
        assertThat(project.getCategory(), equalTo("active"));
        assertThat(project.hasSite(), equalTo(true));
    }

    @Test
    public void projectWithNoSite() throws IOException {
        Project project = service.getProject("spring-integration-dsl-groovy");
        assertThat(project.getId(), equalTo("spring-integration-dsl-groovy"));
        assertThat(project.getName(), equalTo("Spring Integration Groovy DSL"));
        assertThat(project.getRepoUrl(), equalTo("http://github.com/spring-projects/spring-integration-dsl-groovy"));
        assertThat(project.getSiteUrl(), equalTo(""));
        assertThat(project.hasSite(), equalTo(false));
    }

    @Test
    public void getSupportedReferenceDocumentVersionsOrdering() {
        List<ProjectRelease> docVersions = service.getProject("spring-framework").getProjectReleases();
        assertThat(
                docVersions.get(0).getRefDocUrl(),
                equalTo("http://docs.spring.io/spring/docs/4.0.1.BUILD-SNAPSHOT/spring-framework-reference/htmlsingle/"));
        assertThat(docVersions.get(1).getRefDocUrl(),
                equalTo("http://docs.spring.io/spring/docs/4.0.0.RELEASE/spring-framework-reference/htmlsingle/"));
        assertThat(
                docVersions.get(2).getRefDocUrl(),
                equalTo("http://docs.spring.io/spring/docs/3.2.7.BUILD-SNAPSHOT/spring-framework-reference/htmlsingle/"));
        assertThat(docVersions.get(3).getRefDocUrl(),
                equalTo("http://docs.spring.io/spring/docs/3.2.6.RELEASE/spring-framework-reference/htmlsingle/"));
        assertThat(docVersions.get(4).getRefDocUrl(),
                equalTo("http://docs.spring.io/spring/docs/3.1.4.RELEASE/spring-framework-reference/htmlsingle/"));
    }

    @Test
    public void getSupportedApiDocsUrlsOrdering() {
        List<ProjectRelease> docVersions = service.getProject("spring-framework").getProjectReleases();
        assertThat(docVersions.get(0).getApiDocUrl(),
                equalTo("http://docs.spring.io/spring/docs/4.0.1.BUILD-SNAPSHOT/javadoc-api/"));
        assertThat(docVersions.get(1).getApiDocUrl(),
                equalTo("http://docs.spring.io/spring/docs/4.0.0.RELEASE/javadoc-api/"));
        assertThat(docVersions.get(2).getApiDocUrl(),
                equalTo("http://docs.spring.io/spring/docs/3.2.7.BUILD-SNAPSHOT/javadoc-api/"));
        assertThat(docVersions.get(3).getApiDocUrl(),
                equalTo("http://docs.spring.io/spring/docs/3.2.6.RELEASE/javadoc-api/"));
        assertThat(docVersions.get(4).getApiDocUrl(),
                equalTo("http://docs.spring.io/spring/docs/3.1.4.RELEASE/javadoc-api/"));
    }

    @Test
    public void nonAggregatorProject() throws IOException {
        Project project = service.getProject("spring-security");
        assertThat(project.isAggregator(), equalTo(false));
    }

    @Test
    public void aggregatorProject() throws IOException {
        Project project = service.getProject("spring-data");
        assertThat(project.isAggregator(), equalTo(true));
        assertThat(project.getId(), equalTo("spring-data"));
    }

    @Test
    public void projectWithNoGaReleaseHasCorrectOrdering() throws Exception {
        Project project = service.getProject("spring-boot");
        assertThat(project.getProjectReleases().get(0).isCurrent(), equalTo(false));
        assertThat(project.getProjectReleases().get(1).isCurrent(), equalTo(false));
    }

    @Autowired
    private ProjectMetadataRepository repo;

    // Verify we don't have issue with deleting a release that might occur when using
    // @OrderColumn
    // See https://hibernate.atlassian.net/browse/HHH-1268
    @Test
    public void deleteRelease() {
        Project project = service.getProject("spring-framework");
        ProjectRelease release = project.getProjectReleases().remove(0);
        assertThat(release.getVersion(), equalTo("4.0.1.BUILD-SNAPSHOT"));
        service.save(project);

        repo.flush();
    }

    @Test
    public void testProjectsWithLabels() throws Exception {

        Collection<ProjectLabel> labelsForProject = service.getProjectLabelsForProject("spring-framework");

        assertThat(labelsForProject.size(), greaterThan(0));
        assertThat("there should be a label 'web' among the project's labels",
                labelsForProject
                        .stream()
                        .filter(pl -> pl.getLabel().equals("web"))
                        .findAny()
                        .map(x -> true)
                        .orElse(false));
    }

}
