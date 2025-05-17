package org.example.DTO;

import java.time.LocalDateTime;

public class NotificationDTO {
    private long id;
    private Long userId;
    private String message;
    private Long vacancyId;
    private LocalDateTime createdDate;

    public NotificationDTO(long id, Long userId, String message, Long vacancyId, LocalDateTime createdDate) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.vacancyId = vacancyId;
        this.createdDate = createdDate;
    }

    public NotificationDTO() {}
    public Long getNotificationId() {
        return id;
    }
    public void setNotificationId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public Long getVacancyId() { return vacancyId; }
    public void setVacancyId(Long vacancyId) { this.vacancyId = vacancyId; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
}