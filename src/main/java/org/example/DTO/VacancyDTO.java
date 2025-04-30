package org.example.DTO;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import org.example.Model.User;

public class VacancyDTO {
    private Long id;

    @NotEmpty(message = "Название вакансии не должно быть пустым")
    private String title;
    private String description;

    @NotEmpty(message = "Местоположение не должно быть пустым")
    private String location;

    @NotEmpty(message = "Поле зарплата не должно быть пустым")
    private float salary;

    private User employer;

    public VacancyDTO() {}

    public VacancyDTO(String title, String description, String location, float salary, User employer) {
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
