package org.example.DTO;

import org.example.Enum.ApplicationStatus;
import org.example.Model.Vacancy;

public class ApplicationViewDTO {
    private Long id;
    private VacancyDTO vacancy;
    private String status;

    public ApplicationViewDTO(long id, VacancyDTO vacancy, ApplicationStatus status) {
        this.id = id;
        this.vacancy = vacancy;
        this.status = status.name();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public VacancyDTO getVacancy() { return vacancy; }

    public void setVacancy(VacancyDTO vacancy) { this.vacancy = vacancy; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}