package org.example.Mapper;
import org.example.DTO.ResumeDTO;
import org.example.Model.Resume;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ResumeMapper {
    Resume toEntity(ResumeDTO dto);

    @Mapping(source = "user.name", target = "userName")
    @Mapping(source = "user.email", target = "userEmail")
    ResumeDTO toDTO(Resume resume);
}
