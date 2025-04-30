package org.example.Controller;
import org.example.DTO.VacancyDTO;
import org.example.Mapper.UserMapper;
import org.example.Mapper.VacancyMapper;
import org.example.Model.User;
import org.example.Model.Vacancy;
import org.example.Service.UserService;
import org.example.Service.VacancyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/vacancies")
public class VacancyController {
    private static final Logger logger = LoggerFactory.getLogger(VacancyController.class);
    @Autowired
    private VacancyMapper vacancyMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private VacancyService vacancyService;

    @Autowired
    private UserService userService;

    @GetMapping("/addVacancy")
    public String showAddVacancyForm() {
        logger.info("Отображение формы создания вакансии.");
        return "addVacancy";
    }

    @PostMapping("/addVacancy")
    @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
    public String  addVacancy(@RequestParam String title, @RequestParam String description,
                              @RequestParam String location, @RequestParam float salary, @RequestParam  Long employerId) {
        User user = userMapper.toEntity(userService.findUserById(employerId));

        if (user != null) {
            vacancyService.create(title, description, location, salary, userMapper.toDTO(user));
            logger.info("Вакансия успешно создана.");
            return "redirect:/vacancies";
        } else {
            logger.error("Пользователь с id {} не найден", employerId);
            return "error";
        }
    }

    @GetMapping
    public List<Vacancy> getAllVacancies() {
        logger.info("Отображение списка вакансий.");
        return vacancyService.getAllVacancies();
    }

    @PreAuthorize("hasRole('EMPLOYER')")
    @PostMapping("/editVacancy")
    public String editVacancy(@ModelAttribute Long vacancyId, @ModelAttribute VacancyDTO vacancyDTO) {
        vacancyService.edit(vacancyId,  vacancyDTO); //сделать чтобы доступ был только у создателя вакансии и админа
        logger.info("Данные вакансии успешно обновлены.");
        return "redirect:/vacancies";
    }

    @PreAuthorize("hasRole('EMPLOYER')")
    @PostMapping("delete-vacancy")
    public String deleteVacancy(@RequestParam Long vacancyId) {
        vacancyService.delete(vacancyId); //сделать чтобы доступ был только у создателя вакансии и админа
        logger.info("Вакансия успешно удалена.");
        return "Вакансия успешно удалена: "  + vacancyId;
    }

    @PostMapping("/{id}")
    public String getDetails(@RequestParam Long vacancyId) {
        return vacancyService.getDetails(vacancyId);
    }
}
