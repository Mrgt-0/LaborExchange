package org.example.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "vacancies")
public class Vacancy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String location;
    private float salary;

    @ManyToOne
    @JoinColumn(name = "employer_id", nullable = false)
    private User employer;

    public Vacancy() {}

    public Vacancy(String title, String description, String location, float salary, User employer) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.salary = salary;
        this.employer = employer;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public float getSalary() { return salary; }
    public void setSalary(float salary) { this.salary = salary; }

    public User getEmployer() { return employer; }
    public void setEmployer(User employer) { this.employer = employer; }
}
