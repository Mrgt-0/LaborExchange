package org.example.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "resumes")
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "userid", nullable = false)
    private Long userId;

    private String skills;

    private String experience;
    private String education;

    public Resume() {}

    public Resume(Long userId, String title, String skills, String experience, String education) {
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
