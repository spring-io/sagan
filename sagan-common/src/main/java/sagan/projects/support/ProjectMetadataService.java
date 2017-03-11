package sagan.projects.support;

import sagan.projects.Project;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import sagan.projects.ProjectLabel;

@Service
public class ProjectMetadataService {

    private final ProjectMetadataRepository metadataRepository;
    private final ProjectLabelRepository projectLabelRepository ;

    @Autowired
    public ProjectMetadataService(ProjectMetadataRepository metadataRepository,
                                  ProjectLabelRepository projectLabelRepository ) {
        this.metadataRepository = metadataRepository;
        this.projectLabelRepository = projectLabelRepository;
    }

    public List<Project> getProjectsForCategory(String category) {
        return metadataRepository.findByCategory(category);
    }

    public List<String> getProjectIds(){
        return metadataRepository.findAllProjectIds();
    }

    public List<Project> getProjects() {
        return metadataRepository.findAll(new Sort("id"));
    }

    public List<Project> getProjectsWithLabels (){
        return metadataRepository.findAllWithLabels(new Sort("id")) ;
    }

    public List<Project> getProjectsWithReleases() {
        return metadataRepository.findAllWithReleases(new Sort("id"));
    }

    public Project getProject(String id) {
        return metadataRepository.findOne (id);
    }

    public Project save(Project project) {
        return metadataRepository.save(project);
    }

    public void delete(String id) {
        metadataRepository.delete(id);
    }

    public Collection<ProjectLabel> getProjectLabelsForProject(String projectId) {
        return metadataRepository.findOneWithLabels(projectId).getProjectLabels();
    }

    public ProjectLabel getProjectLabel (String la) {
        return this.projectLabelRepository.findByLabel(la);
    }
}

