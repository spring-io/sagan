package sagan.projects.support;

import sagan.projects.Project;
import sagan.projects.ProjectRelease;
import sagan.support.nav.Navigation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sagan.support.nav.Section;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Controller that handles administrative actions for Spring project metadata, e.g. adding
 * new releases, updating documentation urls, etc. Per rules in
 * {@code sagan.SecurityConfig}, authentication is required for all requests. See
 * {@link ProjectsController} for public, read-only operations.
 */
@Controller
@RequestMapping("/admin/projects")
@Navigation(Section.PROJECTS)
class ProjectAdminController {
    private static final String VERSION_PLACEHOLDER = "{version}";
    private static final String VERSION_PATTERN = Pattern.quote(VERSION_PLACEHOLDER);
    private static final List<String> CATEGORIES =
            Collections.unmodifiableList(Arrays.asList("incubator", "active", "attic", "community"));

    private ProjectMetadataService service;

    @Autowired
    public ProjectAdminController(ProjectMetadataService service) {
        this.service = service;
    }

    @RequestMapping(value = "", method = GET)
    public String list(Model model) {
        model.addAttribute("projects", service.getProjects());
        return "admin/project/index";
    }

    @RequestMapping(value = "new", method = GET)
    public String newProject(Model model) {
        ProjectRelease release = new ProjectRelease("1.0.0.BUILD-SNAPSHOT",
                ProjectRelease.ReleaseStatus.SNAPSHOT,
                false,
                "http://docs.spring.io/spring-new/docs/{version}/spring-new/htmlsingle/",
                "http://docs.spring.io/spring-new/docs/{version}/spring-new/html/",
                "http://docs.spring.io/spring-new/docs/{version}/javadoc-api/",
                "org.springframework.new",
                "spring-new");

        Project project = new Project("spring-new",
                "New Spring Project",
                "http://github.com/spring-projects/spring-new",
                "http://projects.spring.io/spring-new",
                new ArrayList<>(Arrays.asList(release)),
                false,
                CATEGORIES.get(0));

        return edit(project, model);
    }

    @RequestMapping(value = "{id}", method = GET)
    public String edit(@PathVariable String id, Model model) {
        Project project = service.getProject(id);
        return edit(project, model);
    }

    @RequestMapping(value = "{id}", method = DELETE)
    public String delete(@PathVariable String id, Model model) {
        service.delete(id);
        return "redirect:./";
    }

    private String edit(Project project, Model model) {
        if (project == null) {
            return "pages/404";
        }

        for (ProjectRelease release : project.getProjectReleases()) {
            String version = release.getVersion();
            release.setApiDocUrl(release.getApiDocUrl().replaceAll(version, VERSION_PLACEHOLDER));
            release.setRefDocUrl(release.getRefDocUrl().replaceAll(version, VERSION_PLACEHOLDER));
            release.setRefDocMultiUrl(release.getRefDocMultiUrl().replaceAll(version, VERSION_PLACEHOLDER));
        }

        List<ProjectRelease> releases = project.getProjectReleases();
        if (!releases.isEmpty()) {
            model.addAttribute("groupId", releases.get(0).getGroupId());
        }
        model.addAttribute("project", project);
        model.addAttribute("categories", CATEGORIES);
        return "admin/project/edit";
    }

    @RequestMapping(value = "{id}", method = POST)
    public String save(@Valid Project project, @RequestParam(defaultValue = "") List<String> releasesToDelete,
                       @RequestParam String groupId) {
        Iterator<ProjectRelease> iReleases = project.getProjectReleases().iterator();
        while (iReleases.hasNext()) {
            ProjectRelease release = iReleases.next();
            if ("".equals(release.getVersion()) || releasesToDelete.contains(release.getVersion())) {
                iReleases.remove();
            }
            release.setGroupId(groupId);
            String version = release.getVersion();
            release.setApiDocUrl(release.getApiDocUrl().replaceAll(VERSION_PATTERN, version));
            release.setRefDocUrl(release.getRefDocUrl().replaceAll(VERSION_PATTERN, version));
            release.setRefDocMultiUrl(release.getRefDocMultiUrl().replaceAll(VERSION_PATTERN, version));

        }
        service.save(project);

        return "redirect:" + project.getId();
    }
}
