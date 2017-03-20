package sagan.docs.support;

import sagan.projects.Project;
import sagan.projects.ProjectRelease;
import sagan.projects.support.ProjectMetadataService;
import sagan.search.support.CrawledWebDocumentProcessor;
import sagan.search.support.CrawlerService;
import sagan.search.support.SearchService;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static sagan.projects.ProjectRelease.ReleaseStatus.*;

public class ProjectDocumentationIndexerTests {

    private CrawlerService crawlerService = mock(CrawlerService.class);
    private ProjectMetadataService metadataService = mock(ProjectMetadataService.class);
    private final SearchService searchService = mock(SearchService.class);
    private ProjectDocumentationIndexer service = new ProjectDocumentationIndexer(crawlerService, searchService,
            metadataService);

    private List<ProjectRelease> documentationList = Arrays.asList(
            new ProjectRelease("3", GENERAL_AVAILABILITY, true, "http://reference.example.com/3",
                    "http://referencemulti.example.com/3", "http://api.example.com/3",
                    "com.example", "example-framework"),
            new ProjectRelease("2", SNAPSHOT, false, "http://reference.example.com/2",
                    "http://referencemulti.example.com/2", "http://api.example.com/2",
                    "com.example", "example-framework"),
            new ProjectRelease("1", SNAPSHOT, false, "http://reference.example.com/1",
                    "http://referencemulti.example.com/1", "http://api.example.com/1",
                    "com.example", "example-framework")
            );

    private Project project = new Project("spring",
            "Spring",
            "http://www.example.com/repo/spring-framework",
            "http://www.example.com/spring-framework",
            documentationList,
            false,
            "release");

    private void assertThatCrawlingIsDoneFor(String url, int linkDepthLevel) {
        verify(crawlerService).crawl(eq(url), eq(linkDepthLevel), any(CrawledWebDocumentProcessor.class));
    }

    @Test
    public void referenceDocsAreIndexed() throws Exception {
        service.indexItem(project);
        int linkDepthLevel = 1;
        assertThatCrawlingIsDoneFor("http://reference.example.com/3", linkDepthLevel);
        assertThatCrawlingIsDoneFor("http://reference.example.com/2", linkDepthLevel);
        assertThatCrawlingIsDoneFor("http://reference.example.com/1", linkDepthLevel);
    }

    @Test
    public void multipageReferenceDocsAreNOTIndexed() throws Exception {
        service.indexItem(project);
        int linkDepthLevel = 1;

        // check multipages are NOT indexed
        verify(crawlerService, never()).crawl(
                eq("http://referencemulti.example.com/3"), eq(linkDepthLevel), any(CrawledWebDocumentProcessor.class));
        verify(crawlerService, never()).crawl(
                eq("http://referencemulti.example.com/2"), eq(linkDepthLevel), any(CrawledWebDocumentProcessor.class));
        verify(crawlerService, never()).crawl(
                eq("http://referencemulti.example.com/1"), eq(linkDepthLevel), any(CrawledWebDocumentProcessor.class));
    }

    @Test
    public void apiDocsAreIndexed() throws Exception {
        service.indexItem(project);
        int linkDepthLevel = 2;
        assertThatCrawlingIsDoneFor("http://api.example.com/3/allclasses-frame.html", linkDepthLevel);
        assertThatCrawlingIsDoneFor("http://api.example.com/2/allclasses-frame.html", linkDepthLevel);
        assertThatCrawlingIsDoneFor("http://api.example.com/1/allclasses-frame.html", linkDepthLevel);
    }

}
