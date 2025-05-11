package org.example.Model;

import jakarta.persistence.*;
import org.example.Enum.ReadStatus;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "userId")
    private Long userId;
    private String message;

    private Long vacancyId;
    private String vacancyTitle;

    public Notification() {}

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

    public String getVacancyTitle() { return vacancyTitle; }
    public void setVacancyTitle(String vacancyTitle) {}
}