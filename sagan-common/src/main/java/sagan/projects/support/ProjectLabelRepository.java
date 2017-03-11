package sagan.projects.support;

import org.springframework.data.jpa.repository.JpaRepository;
import sagan.projects.ProjectLabel;

public interface ProjectLabelRepository extends JpaRepository<ProjectLabel, Long> {

    ProjectLabel findByLabel(String label);
}
