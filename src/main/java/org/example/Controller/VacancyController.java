package org.example.Controller;
import org.example.Repository.UserRepository;
import org.example.Repository.VacancyRepository;
import org.springframework.security.core.Authentication;
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
import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/vacancies")
public class VacancyController {
    private static final Logger logger = LoggerFactory.getLogger(VacancyController.class);

    @Autowired
    private VacancyService vacancyService;

    @Autowired
    private UserService userService;

    @Autowired
    private VacancyMapper vacancyMapper;

    @Autowired
    private UserRepository userRepository;

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
    public String addVacancy(@RequestParam String title,
                             @RequestParam String description,
                             @RequestParam String location,
                             @RequestParam float salary,
                             @RequestParam Long employerId) {
        Optional<User> optionalUser = userRepository.findById(employerId);
        if (optionalUser.isEmpty()) return "error";
        User user = optionalUser.get();

        logger.info("Создаем вакансию: title={}, empId={}", title, employerId);
        vacancyService.create(title, description, location, salary, user);
        return "redirect:/vacancies/my";
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('EMPLOYER')")
    public String showEmployerVacancies(Model model, Principal principal) {
        if (principal == null) {
            logger.error("Попытка доступа к защищенному ресурсу без аутентификации.");
            return "error";
        }

        try {
            UserDTO user = userService.findByEmail(principal.getName());
            if (user == null) {
                logger.error("Пользователь с email {} не найден", principal.getName());
                return "error";
            }

            logger.info("Пользователь {} пытается получить доступ к своим вакансиям.", principal.getName());
            List<Vacancy> myVacancies = vacancyService.findVacancyByEmployerId(user.getId());
            if (myVacancies.isEmpty()) {
                logger.info("У пользователя {} нет созданных вакансий.", user.getName());
            } else {
                logger.info("Найденные вакансии: {}", myVacancies.size());
            }

            model.addAttribute("myVacancies", myVacancies);
            return "employer-vacancy-list";
        } catch (Exception e) {
            logger.error("Ошибка при получении списка вакансий: {}", e.getMessage(), e);
            return "error";
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
    public String showEditVacancy(@PathVariable Long id, Model model, Principal principal) {
        logger.info("Запрос на редактирование вакансии с ID: {}", id);

        UserDTO user = userService.findByEmail(principal.getName());
        if (user == null) {
            logger.error("Пользователь не найден.");
            return "error";
        }

        VacancyDTO vacancyDTO = vacancyMapper.toDTO(vacancyService.findVacancyById(id));
        if (vacancyDTO == null) {
            logger.error("Вакансия с id={} не найдена", id);
            return "error";
        }

        boolean isAdmin = user.getUserTypes().stream()
                .anyMatch(r -> "ROLE_ADMIN".equals(r.getTypeString()));
        boolean isOwner = false;

        if (vacancyDTO.getEmployer() != null) {
            Long employerId = vacancyDTO.getEmployer().getId();
            isOwner = employerId != null && employerId.equals(user.getId());
        }
        if (!isAdmin && !isOwner) {
            logger.warn("Доступ запрещён: пользователь {} не является админом или владельцем вакансии id={}", user.getId(), id);
            return "error";
        }
        model.addAttribute("user", user);
        model.addAttribute("vacancy", vacancyDTO);
        return "vacancy-edit";
    }

    @PostMapping("/vacancy-edit")
    public String editVacancy(@ModelAttribute("vacancy") VacancyDTO vacancyDTO, @RequestParam("employer.id") Long employerId, Principal principal) {
        UserDTO currentUser = userService.findByEmail(principal.getName());
        UserDTO employerUser = userService.findUserById(employerId);

        boolean isAdmin = currentUser.getUserTypes().stream()
                .anyMatch(r -> r.getTypeString().equals("ROLE_ADMIN"));
        boolean isOwner = employerUser.getEmail().equals(currentUser.getEmail());

        if (!isAdmin && !isOwner) return "error";
        Long vacancyId = vacancyDTO.getId();
        vacancyService.edit(vacancyId, vacancyDTO, employerUser);
        return "redirect:/vacancies/my";
    }

    @PostMapping("/delete-vacancy")
    public String deleteVacancy(@RequestParam Long id, Authentication authentication) throws AccessDeniedException {
        Vacancy vacancy = vacancyService.findVacancyById(id);
        if (vacancy == null) return "error";

        String currentUsername = authentication.getName();

        if (!(authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
                || currentUsername.equals(vacancy.getEmployer().getEmail())))
        {
            throw new AccessDeniedException("Нет прав");
        }

        vacancyService.delete(id);
        return "redirect:/vacancies/my";
    }

    @GetMapping("/user-vacancy-view/{id}")
    public String viewUserVacancy(Model model, @PathVariable Long id) {
        VacancyDTO vacancyDTO = vacancyMapper.toDTO(vacancyService.findVacancyById(id));
        UserDTO employer = userService.findUserById(vacancyDTO.getEmployer().getId());
        if(vacancyDTO != null) {
            model.addAttribute("vacancy", vacancyDTO);
            model.addAttribute("employer", employer);
            return "user-vacancy-view";  // возврат имени шаблона
        } else {
            logger.error("Вакансии с id: {} не найдено", id);
            return "error";
        }
    }

    @GetMapping("/employer-vacancy-view/{id}")
    public String viewEmployerVacancy(Model model, @PathVariable Long id) {
        Vacancy vacancy = vacancyService.findVacancyById(id);
        if (vacancy == null) {
            logger.error("Вакансии с id: {} не найдено", id);
            return "error";
        }
        VacancyDTO vacancyDTO = vacancyMapper.toDTO(vacancy);
        UserDTO employer = userService.findUserById(vacancyDTO.getEmployer().getId());

        model.addAttribute("vacancy", vacancyDTO);
        model.addAttribute("employer", employer);
        return "employer-vacancy-view";
    }
}
