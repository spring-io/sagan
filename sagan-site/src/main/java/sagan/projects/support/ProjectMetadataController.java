package sagan.projects.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import sagan.projects.Project;
import sagan.support.JsonPController;

import java.io.IOException;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

/**
 * Controller that handles ajax requests for project metadata, typically from the
 * individual Spring project pages managed via GitHub's "GH Pages" infrastructure at
 * http://projects.spring.io. See https://github.com/spring-projects/gh-pages#readme for
 * more information.
 */
@JsonPController
@RequestMapping("/project_metadata")
class ProjectMetadataController {

    private final ProjectMetadataService service;

    @Autowired
    public ProjectMetadataController(ProjectMetadataService service) {
        this.service = service;
    }

    @RequestMapping(method = {GET})
    public List<Project> projects() {
        return service.getProjectsWithLabels();
    }

    @RequestMapping(value = "/projects", method = {GET})
    public List<String> projectIds() {
        return service.getProjectIds();
    }

    @RequestMapping(value = "/{projectId}", method = {GET, HEAD})
    public Project projectMetadata(@PathVariable("projectId") String projectId) throws IOException {
        return service.getProject(projectId);
    }
}
