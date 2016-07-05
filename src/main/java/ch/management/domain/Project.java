package ch.management.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Project.
 */
@Entity
@Table(name = "project")
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "current_month_hours")
    private Double currentMonthHours;

    @Column(name = "total_hours")
    private Double totalHours;

    @OneToMany(mappedBy = "project")
    @JsonIgnore
    private Set<WorkTime> workTimes = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getCurrentMonthHours() {
        return currentMonthHours;
    }

    public void setCurrentMonthHours(Double currentMonthHours) {
        this.currentMonthHours = currentMonthHours;
    }

    public Double getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(Double totalHours) {
        this.totalHours = totalHours;
    }

    public Set<WorkTime> getWorkTimes() {
        return workTimes;
    }

    public void setWorkTimes(Set<WorkTime> workTimes) {
        this.workTimes = workTimes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Project project = (Project) o;
        if(project.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, project.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Project{" +
            "id=" + id +
            ", title='" + title + "'" +
            ", description='" + description + "'" +
            ", currentMonthHours='" + currentMonthHours + "'" +
            ", totalHours='" + totalHours + "'" +
            '}';
    }
}
