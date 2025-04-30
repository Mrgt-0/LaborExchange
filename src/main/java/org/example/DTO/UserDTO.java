package org.example.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.example.Enum.UserType;

public class UserDTO {
    private Long id;

    @NotEmpty(message = "Имя пользователя не должно быть пустым")
    private String name;
    @NotEmpty(message = "Фамилия пользователя не должно быть пустым")
    private String lastname;

    @NotEmpty(message = "Email не должен быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;

    @NotEmpty(message = "Пароль не должен быть пустым")
    @Size(min = 8, message = "Пароль должен содержать как минимум 8 символов")
    private String password;

    @NotEmpty(message = "Тип пользователя не должен быть пустым")
    private String usertype;

    public UserDTO() {}

    public UserDTO(String name, String lastname, String email, String password, String usertype) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.usertype = usertype;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getUsertype() { return usertype; }
    public void setUsertype(String usertype) { this.usertype = usertype; }
}
