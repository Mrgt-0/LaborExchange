package org.example.DTO;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.example.Enum.ApplicationStatus;
import org.example.Model.Vacancy;

public class ApplicationDTO {
    private long id;
    private Long userId;
    private Vacancy vacancy;
    private ApplicationStatus status;

    public ApplicationDTO() {}

    public ApplicationDTO(Long userId, Vacancy vacancy) {
        this.userId = userId;
        this.vacancy = vacancy;
        this.status = ApplicationStatus.PENDING;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUser(Long userId) { this.userId = userId; }

    public Vacancy getVacancy() { return vacancy; }
    public void setVacancy(Vacancy vacancy) { this.vacancy = vacancy; }

    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }

    public void submit() {
        this.status = ApplicationStatus.PENDING;
    }
    // Метод отзыва отклика
    public void withdraw() {
        this.status = ApplicationStatus.WITHDRAWN;
    }
}
