package org.example.Controller;

import jakarta.transaction.SystemException;
import org.example.DTO.ResumeDTO;
import org.example.DTO.UserDTO;
import org.example.Mapper.ResumeMapper;
import org.example.Model.Resume;
import org.example.Service.ResumeService;
import org.example.Service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/resumes")
public class ResumeController {
    private static final Logger logger = LoggerFactory.getLogger(ResumeController.class);

    @Autowired
    private ResumeService resumeService;

    @Autowired
    private UserService userService;

    @Autowired
    private ResumeMapper resumeMapper;

    @GetMapping("/resume-list")
    public String showResumeList(Model model, Principal principal) throws SystemException {
        logger.info("Запрос списка резюме от пользователя: {}", principal.getName());

        UserDTO user = userService.findByEmail(principal.getName());
        if (user != null) {
            model.addAttribute("user", user);

            List<ResumeDTO> resumes = resumeService.findResumesByUserId(user.getId());
            model.addAttribute("resumes", resumes);
            return "resume-list";
        } else {
            logger.error("Пользователь не найден.");
            return "error";
        }
    }

    @GetMapping("resume-add")
    public String showAddResumeForm(Model model, Principal principal) {
        UserDTO user = userService.findByEmail(principal.getName());
        if(user != null) {
            model.addAttribute("user", user);
            return "resume-add";
        }else {
            logger.error("Пользователь не найден.");
            return "error";
        }
    }

    @PostMapping("/resume-add")
    public String addResume(@RequestParam Long userId, @RequestParam String title,
                            @RequestParam String skills, @RequestParam String experience, @RequestParam String education) {
        logger.info("Создание резюме. UserId: {}", userId);

        resumeService.upload(userId, title, skills, experience, education);
        logger.info("Резюме успешно опубликовано.");
        return "redirect:/resumes/resume-list";
    }

    @GetMapping("resume-edit/{id}")
    public String showEditResumeForm(@PathVariable Long id, Model model, Principal principal) throws SystemException {
        logger.info("Запрос на редактирование резюме с ID: {}", id);
        UserDTO user = userService.findByEmail(principal.getName());
        if (user != null) {
            ResumeDTO resume = resumeService.findResumeById(id);

            if (resume != null && resume.getUserId().equals(user.getId())) {
                model.addAttribute("user", user);
                model.addAttribute("resume", resume);
                return "resume-edit";
            } else {
                logger.error("Резюме с ID {} не найдено или не принадлежит пользователю.", id);
                return "error";
            }
        } else {
            logger.error("Пользователь не найден");
            return "error";
        }
    }

    @PostMapping("/resume-edit")
    public String updateResume(@RequestParam Long resumeId, @ModelAttribute ResumeDTO resumeDTO) throws SystemException {
        resumeService.update(resumeId, resumeDTO);
        logger.info("Данные резюме успешно обновлены.");
        return "redirect:/resumes/resume-list";
    }

    @GetMapping("/user-resume-summary/{id}")
    public String viewUserResume(@PathVariable Long id, Model model) throws SystemException {
        ResumeDTO resume = resumeService.findResumeById(id);
        if (resume != null) {
            model.addAttribute("resume", resume);
            return "user-resume-summary";
        } else {
            return "redirect:/resumes/resume-list";
        }
    }

    @GetMapping("/employer-resume-summary/{id}")
    public String viewEmployerResume(@PathVariable Long id, Model model) throws SystemException {
        ResumeDTO resume = resumeService.findResumeById(id);
        if (resume != null) {
            model.addAttribute("resume", resume);
            return "employer-resume-summary";
        } else {
            return "redirect:/resumes/all";
        }
    }

    @GetMapping("/all")
    public String showAllResumes(@RequestParam(required = false) String title, Model model) throws SystemException {
        List<Resume> resumes;
        if (title == null || title.isBlank())
            resumes = resumeService.getAllResumes();
        else
            resumes = resumeService.findByTitleContainingIgnoreCase(title);

        model.addAttribute("resumes", resumes);
        model.addAttribute("searchTitle", title);
        return "all-resume-list";
    }

    @PostMapping("/delete-resume")
    public String deleteResume(@RequestParam Long id) throws SystemException {
        resumeService.delete(id);
        logger.info("Резюме успешно удалено.");
        return "redirect:/resumes/resume-list";
    }
}
