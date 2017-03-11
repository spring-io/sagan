package sagan.projects.support;

import sagan.projects.Project;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Repository
public interface ProjectMetadataRepository extends JpaRepository<Project, String> {
    List<Project> findByCategory(String category);

    @Query("SELECT DISTINCT p.id FROM Project p")
    List<String> findAllProjectIds() ;

    @Query("SELECT DISTINCT p FROM Project p JOIN FETCH p.releaseList")
    List<Project> findAllWithReleases(Sort sort);

    @Query("SELECT DISTINCT p FROM Project p JOIN FETCH p.projectLabels")
    List<Project> findAllWithLabels(Sort id);

    @Query("SELECT DISTINCT p FROM Project p JOIN FETCH p.projectLabels where p.id = ?1 ")
    Project findOneWithLabels ( String id) ;
}
