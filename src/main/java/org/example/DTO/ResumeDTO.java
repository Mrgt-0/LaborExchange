package org.example.DTO;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.example.Model.User;

public class ResumeDTO {
    private long id;
    private String title;
    private Long userId;
    private String skills;
    private String experience;
    private String education;

    public ResumeDTO() {}

    public ResumeDTO(Long userId, String title, String skills, String experience, String education) {
        this.userId = userId;
        this.title = title;
        this.skills = skills;
        this.experience = experience;
        this.education = education;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }

    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }
}
