package org.example.Controller;
import org.example.DTO.UserDTO;
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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/vacancies")
public class VacancyController {
    private static final Logger logger = LoggerFactory.getLogger(VacancyController.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private VacancyService vacancyService;

    @Autowired
    private UserService userService;

    @Autowired
    private VacancyMapper vacancyMapper;

    @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
    @GetMapping("/vacancy-add")
    public String showAddVacancyForm(Model model, Principal principal) {
        logger.info("Отображение формы создания вакансии.");

        UserDTO user = userService.findByEmail(principal.getName());

        if (user != null) {
            model.addAttribute("vacancy", new Vacancy());
            model.addAttribute("employerId", user.getId());
            return "vacancy-add";
        } else {
            logger.error("Пользователь не найден.");
            return "error";
        }
    }

    @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
    @PostMapping("/vacancy-add")
    public String  addVacancy(@RequestParam String title, @RequestParam String description,
                              @RequestParam String location, @RequestParam float salary, @RequestParam  Long employerId) {
        logger.info("Создание вакансии.");
        User user = userMapper.toEntity(userService.findUserById(employerId));

        vacancyService.create(title, description, location, salary, userMapper.toDTO(user));
        logger.info("Вакансия успешно создана.");
        return "redirect:/vacancies/my";
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('EMPLOYER')")
    public String showEmployerVacancies(Model model, Principal principal) {
        if (principal == null) {
            logger.error("Попытка доступа к защищенному ресурсу без аутентификации.");
            return "error";  // добавьте страницу error
        }

        try {
            UserDTO user = userService.findByEmail(principal.getName());
            if (user == null) {
                logger.error("Пользователь с email {} не найден", principal.getName());
                return "error";  // добавьте страницу error
            }

            logger.info("Пользователь {} пытается получить доступ к своим вакансиям.", principal.getName());
            List<Vacancy> myVacancies = vacancyService.findVacancyByEmployerId(user.getId());
            if (myVacancies.isEmpty()) {
                logger.info("У пользователя {} нет созданных вакансий.", user.getName());
            } else {
                logger.info("Найденные вакансии: {}", myVacancies.size());
            }

            model.addAttribute("myVacancies", myVacancies);
            return "employer-vacancy-list";  // проверьте, что этот шаблон готов к работе
        } catch (Exception e) {
            logger.error("Ошибка при получении списка вакансий: {}", e.getMessage(), e);
            return "error";  // добавьте страницу error
        }
    }

    // Страница для обычных пользователей для просмотра всех вакансий
    @GetMapping("/all")
    public String showAllVacancies(Model model) {
        List<Vacancy> vacancies = vacancyService.getAllVacancies();
        model.addAttribute("vacancies", vacancies);
        return "all-vacancy-list";
    }

    @GetMapping("/vacancy-edit/{id}")
    @PreAuthorize("hasRole('ADMIN') or (authentication.principal.username == #username and #vacancy.employer.id == authentication.principal.id)")
    public String showEditVacancy(@PathVariable Long id, Model model, Principal principal) {
        logger.info("Запрос на редактирование вакансии с ID: {}", id);
        UserDTO user = userService.findByEmail(principal.getName());
        if (user != null) {
            VacancyDTO vacancyDTO = vacancyMapper.toDTO(vacancyService.findVacancyById(id));

            if (vacancyDTO != null && vacancyDTO.getEmployer().getId().equals(user.getId())) {
                model.addAttribute("user", user);
                model.addAttribute("vacancy", vacancyDTO);
                return "vacancy-edit";
            } else {
                logger.error("Вакансия с ID {} не найдена или не принадлежит пользователю.", id);
                return "error";
            }
        }else{
            logger.error("Пользователь не найден.");
            return "error";
        }
    }

    @PreAuthorize("hasRole('ADMIN') or (authentication.principal.username == #vacancyDTO.employer.email)")
    @PostMapping("/vacancy-edit")
    public String editVacancy(@ModelAttribute VacancyDTO vacancyDTO) {
        Long vacancyId = vacancyDTO.getId();
        vacancyService.edit(vacancyId,  vacancyDTO); //сделать чтобы доступ был только у создателя вакансии и админа
        logger.info("Данные вакансии успешно обновлены.");
        return "redirect:/vacancy-list";
    }

    @PreAuthorize("hasRole('ADMIN') or (authentication.principal.username == #username and #vacancy.employer.id == authentication.principal.id)")
    @PostMapping("delete-vacancy")
    public String deleteVacancy(@RequestParam Long id) {
        vacancyService.delete(id); //сделать чтобы доступ был только у создателя вакансии и админа
        logger.info("Вакансия успешно удалена.");
        return "redirect:/vacancy-list";
    }

    @GetMapping("/vacancy-view/{id}")
    public String viewVacancy(Model model, @PathVariable Long id) {
        VacancyDTO vacancyDTO = vacancyMapper.toDTO(vacancyService.findVacancyById(id));
        if(vacancyDTO != null) {
            model.addAttribute("vacancy", vacancyDTO);
            return "vacancy-view";
        }else{
            logger.error("Вакансии с id: {} не найдено", id);
            return "error";
        }
    }
}
