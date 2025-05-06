package org.example.Model;

import jakarta.persistence.*;
import org.example.Enum.UserTypeEnum;

import java.util.Objects;

@Entity
@Table(name = "user_types")
public class UserType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", unique = true)
    private UserTypeEnum type;

    public UserType() {}

    public UserType(UserTypeEnum type) {
        this.type = type;
    }

    public UserTypeEnum getType() { return type; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserType)) return false;
        UserType userType = (UserType) o;
        return this.id != null && this.id.equals(userType.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getTypeString() {
        return type.toString();
    }
}
