package sagan.docs.support;

import sagan.Indexer;
import sagan.projects.Project;
import sagan.projects.ProjectRelease;
import sagan.projects.support.ProjectMetadataService;
import sagan.search.support.CrawledWebDocumentProcessor;
import sagan.search.support.CrawlerService;
import sagan.search.support.SearchService;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectDocumentationIndexer implements Indexer<Project> {

    private static final Log logger = LogFactory.getLog(ProjectDocumentationIndexer.class);

    private final ProjectMetadataService metadataService;
    private final CrawlerService crawlerService;
    private final SearchService searchService;

    @Autowired
    public ProjectDocumentationIndexer(CrawlerService crawlerService, SearchService searchService,
                                       ProjectMetadataService metadataService) {
        this.searchService = searchService;
        this.metadataService = metadataService;
        this.crawlerService = crawlerService;
    }

    @Override
    public Iterable<Project> indexableItems() {
        return metadataService.getProjectsWithReleases();
    }

    @Override
    public void indexItem(Project project) {
        logger.debug("Indexing project: " + project.getId());

        List<String> projectVersions = project.getProjectReleases().stream()
                .filter(projectRelease -> projectRelease.isCurrent())
                .map(ProjectRelease::getVersion)
                .collect(Collectors.toList());

        searchService.removeOldProjectEntriesFromIndex(project.getId(), projectVersions);

        for (ProjectRelease version : project.getProjectReleases()) {
            if (version.getApiDocUrl().isEmpty()) {
                String message =
                        String.format(
                                "Unable to index API doc for project id '%s' and version '%s' since API doc URL is empty",
                                project.getId(), version.getVersion());
                logger.warn(message);
            } else {
                String apiDocUrl = version.getApiDocUrl().replace("/index.html", "") + "/allclasses-frame.html";
                ApiDocumentMapper apiDocumentMapper = new ApiDocumentMapper(project, version);
                CrawledWebDocumentProcessor apiDocProcessor =
                        new CrawledWebDocumentProcessor(searchService, apiDocumentMapper);
                crawlerService.crawl(apiDocUrl, 2, apiDocProcessor);

                String refDocUrl = version.getRefDocUrl();
                ReferenceDocumentSearchEntryMapper documentMapper =
                        new ReferenceDocumentSearchEntryMapper(project, version);
                CrawledWebDocumentProcessor refDocProcessor =
                        new CrawledWebDocumentProcessor(searchService, documentMapper);
                crawlerService.crawl(refDocUrl, 1, refDocProcessor);
            }
        }
    }

    @Override
    public String counterName() {
        return "projects";
    }

    @Override
    public String getId(Project project) {
        return project.getId();
    }
}
