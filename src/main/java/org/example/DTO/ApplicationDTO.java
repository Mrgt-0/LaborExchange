package org.example.DTO;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.example.Enum.ApplicationStatus;

public class ApplicationDTO {
    private long id;
    private Long userId;
    private Long vacancyId;
    private ApplicationStatus status;

    public ApplicationDTO() {}

    public ApplicationDTO(Long userId, Long vacancyId) {
        this.userId = userId;
        this.vacancyId = vacancyId;
        this.status = ApplicationStatus.PENDING;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUser(Long userId) { this.userId = userId; }

    public Long getVacancyId() { return vacancyId; }
    public void setVacancyId(Long vacancyId) { this.vacancyId = vacancyId; }

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
