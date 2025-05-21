package org.example.DTO;
import org.example.Enum.UserTypeEnum;

public class UserTypeDTO {
    private Long id;
    private UserTypeEnum type;

    public UserTypeDTO() {}
    public UserTypeEnum getType() { return type; }
}
