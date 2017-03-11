package sagan.projects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class ProjectLabel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Project> getProjects() {
        return projects;
    }

    public void setProjects(Set<Project> projects) {
        this.projects = projects;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public ProjectLabel(String label) {
        this.label = label;
    }


    @JsonIgnore
    @JoinTable(name = "projecttolabel", joinColumns = @JoinColumn(name = "project_label_id"),
            inverseJoinColumns = @JoinColumn(name = "project_id"))
    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Project> projects = new HashSet<>();

    @Column(unique = true)
    private String label;

    @SuppressWarnings("unused")
    private ProjectLabel() {
    }

    public ProjectLabel(Set<Project> projects, String label) {
        this.projects = projects;
        this.label = label;
    }

    @Override
    public String toString() {
        return "ProjectLabel{" +
                "id=" + id +
                ", label='" + label + '\'' +
                '}';
    }
}
