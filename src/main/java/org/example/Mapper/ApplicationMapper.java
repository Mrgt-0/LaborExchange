package org.example.Mapper;
import org.example.DTO.ApplicationDTO;
import org.example.Model.Application;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {
    Application toEntity(ApplicationDTO applicationDTO);
    ApplicationDTO toDTO(Application application);
}
