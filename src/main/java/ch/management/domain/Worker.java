package ch.management.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Worker.
 */
@Entity
@Table(name = "worker")
public class Worker implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "current_month_hours")
    private Double currentMonthHours;

    @Column(name = "total_hours")
    private Double totalHours;

    @Column(name = "total_expenses")
    private Double totalExpenses;

    @OneToMany(mappedBy = "worker")
    @JsonIgnore
    private Set<WorkTime> workTimes = new HashSet<>();

    @OneToMany(mappedBy = "worker")
    @JsonIgnore
    private Set<Expense> expenses = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Double getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(Double totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public Set<WorkTime> getWorkTimes() {
        return workTimes;
    }

    public void setWorkTimes(Set<WorkTime> workTimes) {
        this.workTimes = workTimes;
    }

    public Set<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(Set<Expense> expenses) {
        this.expenses = expenses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Worker worker = (Worker) o;
        if(worker.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, worker.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Worker{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", currentMonthHours='" + currentMonthHours + "'" +
            ", totalHours='" + totalHours + "'" +
            ", totalExpenses='" + totalExpenses + "'" +
            '}';
    }
}
