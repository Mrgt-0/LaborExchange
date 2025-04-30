package org.example.Model;

import jakarta.persistence.*;
import org.example.Enum.ApplicationStatus;

@Entity
@Table(name = "applications")
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "vacancy_id")
    private Long vacancyId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    public Application() {}

    public Application(Long userId, Long vacancyId, ApplicationStatus status) {
        this.userId = userId;
        this.vacancyId = vacancyId;
        this.status = status;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUser(Long userId) { this.userId = userId; }

    public Long getVacancyId() { return vacancyId; }
    public void setVacancyId(Long vacancyId) { this.vacancyId = vacancyId; }

    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }
}
