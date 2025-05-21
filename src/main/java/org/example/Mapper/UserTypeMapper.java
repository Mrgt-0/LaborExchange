package org.example.Mapper;
import org.example.DTO.UserTypeDTO;
import org.example.Model.UserType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserTypeMapper {
    UserType toEntity(UserTypeDTO userType);
    UserTypeDTO toDTO(UserType userType);
}
