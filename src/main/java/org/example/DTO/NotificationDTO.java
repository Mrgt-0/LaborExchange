package org.example.DTO;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.example.Enum.ReadStatus;

public class NotificationDTO {
    private long id;
    private Long userId;
    private String message;
    private ReadStatus readStatus;

    public NotificationDTO(Long userId, String message) {
        this.userId = userId;
        this.message = message;
    }
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
}
