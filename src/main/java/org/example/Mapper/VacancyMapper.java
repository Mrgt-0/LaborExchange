package org.example.Mapper;
import org.example.DTO.VacancyDTO;
import org.example.Model.Vacancy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VacancyMapper {
    Vacancy toEntity(VacancyDTO vacancyDTO);
    VacancyDTO toDTO(Vacancy vacancy);
}
