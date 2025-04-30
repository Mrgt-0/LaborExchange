package org.example.Model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String lastname;
    private String email;
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_user_types",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "user_type_id")
    )
    private Set<UserType> userTypes;

    public User() {}

    public User(String name, String lastname, String email, String password, Set<UserType> userTypes) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.userTypes = userTypes;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLastname() { return lastname; }
    public void setLastname(String lastName) { this.lastname = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Set<UserType> getUserTypes() { return userTypes; }
    public void setUserTypes(Set<UserType> userTypes) { this.userTypes = userTypes; }
}