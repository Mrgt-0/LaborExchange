package org.example.Service;

import jakarta.transaction.Transactional;
import org.example.DTO.UserDTO;
import org.example.DTO.VacancyDTO;
import org.example.Mapper.UserMapper;
import org.example.Mapper.VacancyMapper;
import org.example.Model.User;
import org.example.Model.Vacancy;
import org.example.Repository.UserRepository;
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
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Transactional
    public void create(String title, String description, String location, float salary, User employer) {
        Vacancy vacancy = new Vacancy();
        vacancy.setTitle(title);
        vacancy.setDescription(description);
        vacancy.setLocation(location);
        vacancy.setSalary(salary);
        vacancy.setEmployer(employer);
        vacancyRepository.save(vacancy);
        logger.info("Вакансия успешно создана: {}", vacancy.getTitle());
    }

    public List<Vacancy> getAllVacancies() {
        return vacancyRepository.findAll();
    }

    @Transactional
    public void edit(Long vacancyId, VacancyDTO vacancyDTO, UserDTO employerUser) {
        Vacancy vacancy = vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new RuntimeException("Vacancy not found"));

        vacancy.setTitle(vacancyDTO.getTitle());
        vacancy.setDescription(vacancyDTO.getDescription());
        vacancy.setLocation(vacancyDTO.getLocation());
        vacancy.setSalary(vacancyDTO.getSalary());

        User employer = userRepository.findById(employerUser.getId())
                .orElseThrow(() -> new RuntimeException("Employer not found"));

        vacancy.setEmployer(employer);
        vacancyRepository.save(vacancy);
    }

    public void delete(Long vacancyId) {
        if(vacancyRepository.findById(vacancyId).isPresent()) {
            vacancyRepository.deleteById(vacancyId);
            logger.info("Вакансия успешно удалена: {}",  vacancyId);
        }else
            logger.error("Вакансия не найдена.");
    }
//не используется
    public String getDetails(Long vacancyId) {
        Optional<Vacancy> vacancy = vacancyRepository.findById(vacancyId);
        return vacancy.get().getTitle() + "\n" +
                vacancy.get().getDescription() + "\n" +
                vacancy.get().getLocation() + "\n" +
                vacancy.get().getSalary() + "\n" +
                vacancy.get().getEmployer();
    }

    public Vacancy findVacancyById(Long vacancyId) {
        return  vacancyRepository.findById(vacancyId).get();
    }

    public List<Vacancy> findVacancyByEmployerId(Long employerId) {
        logger.info("Запрос вакансий для работодателя с ID: {}", employerId);
        List<Vacancy> vacancies = vacancyRepository.findByEmployerId(employerId);
        logger.info("Найденные вакансии: {}", vacancies);
        return vacancies;
    }

    public List<Vacancy> findByTitleContainingIgnoreCase(String title) {
        return vacancyRepository.findByTitleContainingIgnoreCase(title);
    }
}