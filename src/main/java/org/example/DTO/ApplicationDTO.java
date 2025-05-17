package org.example.DTO;
import org.example.Enum.ApplicationStatus;
import org.example.Model.User;
import org.example.Model.Vacancy;

public class ApplicationDTO {
    private long id;
    private Long userId;
    private Vacancy vacancy;
    private ApplicationStatus status;
    private UserDTO applicant;

    public ApplicationDTO() {}

    public ApplicationDTO(Long userId, Vacancy vacancy, UserDTO user) {
        this.userId = userId;
        this.vacancy = vacancy;
        this.status = ApplicationStatus.PENDING;
        this.applicant = user;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUser(Long userId) { this.userId = userId; }

    public Vacancy getVacancy() { return vacancy; }
    public void setVacancy(Vacancy vacancy) { this.vacancy = vacancy; }

    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }

    public UserDTO getApplicant() { return applicant; }
    public void setApplicant(UserDTO applicant) { this.applicant = applicant; }

    public void submit() {
        this.status = ApplicationStatus.PENDING;
    }
    // Метод отзыва отклика
    public void withdraw() {
        this.status = ApplicationStatus.WITHDRAWN;
    }
}
