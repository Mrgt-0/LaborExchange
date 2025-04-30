package org.example.Service;

import jakarta.transaction.Transactional;
import org.example.DTO.UserDTO;
import org.example.DTO.VacancyDTO;
import org.example.Mapper.UserMapper;
import org.example.Mapper.VacancyMapper;
import org.example.Model.Vacancy;
import org.example.Repository.VacancyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VacancyService {
    private static final Logger logger = LoggerFactory.getLogger(VacancyService.class);

    @Autowired
    private VacancyRepository vacancyRepository;

    @Autowired
    private VacancyMapper vacancyMapper;

    @Autowired
    private UserMapper userMapper;

    public void create(String title, String description, String location, float salary,  UserDTO employer) {
        VacancyDTO vacancyDTO = new VacancyDTO();
        vacancyDTO.setTitle(title);
        vacancyDTO.setDescription(description);
        vacancyDTO.setLocation(location);
        vacancyDTO.setSalary(salary);
        vacancyDTO.setEmployer(userMapper.toEntity(employer));
        vacancyRepository.save(vacancyMapper.toEntity(vacancyDTO));
        logger.info("Вакансия успешно создана: {}",  vacancyDTO.getTitle());
    }

    public List<Vacancy> getAllVacancies() {
        return vacancyRepository.findAll();
    }

    @Transactional
    public void edit(Long vacancyId, VacancyDTO updateVacancy) {
        vacancyRepository.findById(vacancyId)
                .map(vacancy ->  {
                    vacancy.setTitle(updateVacancy.getTitle());
                    vacancy.setDescription(updateVacancy.getDescription());
                    vacancy.setLocation(updateVacancy.getLocation());
                    vacancy.setSalary(updateVacancy.getSalary());
                    return vacancyRepository.save(vacancy);
                })
                .orElseThrow(() -> new RuntimeException("Вакансия не найдена."));
    }

    public void delete(Long vacancyId) {
        if(vacancyRepository.findById(vacancyId).isPresent()) {
            vacancyRepository.deleteById(vacancyId);
            logger.info("Вакансия успешно удалена: {}",  vacancyId);
        }else
            logger.error("Вакансия не найдена.");
    }

    public String getDetails(Long vacancyId) {
        Optional<Vacancy> vacancy = vacancyRepository.findById(vacancyId);
        return vacancy.get().getTitle() + "\n" +
                vacancy.get().getDescription() + "\n" +
                vacancy.get().getLocation() + "\n" +
                vacancy.get().getSalary() + "\n" +
                vacancy.get().getEmployer();
    }
}