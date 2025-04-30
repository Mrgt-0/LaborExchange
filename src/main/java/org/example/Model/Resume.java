package org.example.Model;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "resumes")
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    private String skills;

    private String experience;
    private String education;

    public Resume() {}

    public Resume(User user, String title, String skills, String experience, String education) {
        this.user = user;
        this.title = title;
        this.skills = skills;
        this.experience = experience;
        this.education = education;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }

    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }
}
