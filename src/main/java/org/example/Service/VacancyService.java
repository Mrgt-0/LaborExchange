package org.example.Service;
import jakarta.transaction.Transactional;
import org.example.DTO.UserDTO;
import org.example.DTO.VacancyDTO;
import org.example.Mapper.UserMapper;
import org.example.Mapper.VacancyMapper;
import org.example.Model.User;
import org.example.Model.Vacancy;
import org.example.Repository.ApplicationRepository;
import org.example.Repository.UserRepository;
import org.example.Repository.VacancyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VacancyService {
    private static final Logger logger = LoggerFactory.getLogger(VacancyService.class);
    @Autowired
    private VacancyRepository vacancyRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private VacancyMapper vacancyMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ApplicationRepository applicationRepository;

    @Transactional
    public void create(String title, String description, String location, float salary, UserDTO employer) {
        VacancyDTO vacancy = new VacancyDTO();
        vacancy.setTitle(title);
        vacancy.setDescription(description);
        vacancy.setLocation(location);
        vacancy.setSalary(salary);
        vacancy.setEmployer(userMapper.toEntity(employer));
        vacancyRepository.save(vacancyMapper.toEntity(vacancy));
        logger.info("Вакансия успешно создана: {}", vacancy.getTitle());
        String message = "Опубликована новая вакансия.";
        notificationService.notifyAllUsersAboutNewVacancy(vacancy, message);
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

    @Transactional
    public void delete(Long vacancyId) {
        if(vacancyRepository.findById(vacancyId).isPresent()) {
            applicationRepository.deleteByVacancyId(vacancyId);
            vacancyRepository.deleteById(vacancyId);
            logger.info("Вакансия успешно удалена: {}",  vacancyId);
        }else
            logger.error("Вакансия не найдена.");
    }

    public Vacancy getVacancyById(Long vacancyId) {
        return vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new IllegalArgumentException("Вакансия не найдена с ID: " + vacancyId));
    }

    public List<Vacancy> findVacancyByEmployerId(Long employerId) {
        logger.info("Запрос вакансий для работодателя с ID: {}", employerId);
        List<Vacancy> vacancies = vacancyRepository.findByEmployerId(employerId);
        logger.info("Найденные вакансии: {}", vacancies);
        return vacancies;
    }

    public List<VacancyDTO> findByTitleContainingIgnoreCase(String title) {
        return vacancyRepository.findByTitleContainingIgnoreCase(title)
                .stream().map(resume -> vacancyMapper.toDTO(resume)).collect(Collectors.toList());
    }
}