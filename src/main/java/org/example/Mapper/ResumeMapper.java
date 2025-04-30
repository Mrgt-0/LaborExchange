package org.example.Mapper;
import org.example.DTO.ResumeDTO;
import org.example.Model.Resume;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ResumeMapper {
    Resume toEntity(ResumeDTO dto);
    ResumeDTO toDTO(Resume resume);
}
