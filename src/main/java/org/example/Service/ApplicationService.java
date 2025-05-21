package org.example.Service;
import jakarta.transaction.Transactional;
import org.example.DTO.ApplicationViewDTO;
import org.example.Enum.ApplicationStatus;
import org.example.Mapper.VacancyMapper;
import org.example.Model.Application;
import org.example.Model.Vacancy;
import org.example.Repository.ApplicationRepository;
import org.example.Repository.VacancyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationService {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationService.class);
    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private VacancyRepository vacancyRepository;
    @Autowired
    private VacancyMapper vacancyMapper;
    @Autowired
    private ResumeService resumeService;

    // Подать новый отклик
    @Transactional
    public void submitApplication(Long userId, Vacancy vacancy) {
        if (vacancy == null || vacancy.getId() == null)
            throw new IllegalArgumentException("Вакансия не может быть null");

        // Проверка на наличие резюме у пользователя
        if (!resumeService.userHasResume(userId))
            throw new IllegalStateException("Вы не можете подать отклик, так как у вас нет резюме.");

        // Проверка существующих откликов на вакансию
        List<Application> existingApplications = applicationRepository.findListByUserIdAndVacancyId(userId, vacancy.getId());
        if (existingApplications.stream().anyMatch(app -> app.getStatus() == ApplicationStatus.PENDING || app.getStatus() == ApplicationStatus.ACCEPTED))
            throw new IllegalStateException("Вы уже подали отклик на эту вакансию.");

        Application application = new Application();
        application.setUserId(userId);
        application.setVacancy(vacancy);
        application.setStatus(ApplicationStatus.PENDING);
        applicationRepository.save(application);
        notificationService.createAndSendNotification(userId, "Вы успешно откликнулись на вакансию.", vacancy.getId());
    }
    // Отозвать отклик
    public void withdrawApplication(Long applicationId, Long userId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Отклик не найден"));
        if (!application.getUserId().equals(userId))
            throw new SecurityException("Недостаточно прав");

        application.withdraw();
        applicationRepository.save(application);
        notificationService.createAndSendNotification(userId, "Вы отозвали ваш отклик на вакансию.", null);
    }

    public List<Application> getApplicationsForEmployer(Long employerId) {
        List<Vacancy> vacancies = vacancyRepository.findByEmployerId(employerId);
        List<Application> applications = new ArrayList<>();
        for (Vacancy vacancy : vacancies)
            applications.addAll(applicationRepository.findByVacancyId(vacancy.getId()));

        logger.info("Полученные отклики для работодателя с ID {}: {}", employerId, applications);
        return applications;
    }

    public List<Application> getApplicationsForVacancy(Long vacancyId) {
        return applicationRepository.findByVacancyId(vacancyId);
    }

    @Transactional
    public void updateApplicationStatus(Long applicationId, ApplicationStatus newStatus) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Отклик не найден"));
        application.setStatus(newStatus);
        applicationRepository.save(application);
        String message = switch (newStatus) {
            case ACCEPTED -> "Ваш отклик на вакансию принят.";
            case REJECTED -> "Ваш отклик на вакансию отклонён.";
            default -> "Статус отклика обновлён.";
        };
        // Отправляем уведомление соискателю
        notificationService.createAndSendNotification(application.getUserId(), message, application.getVacancy().getId());
    }

    // Получение откликов по пользователю
    public List<ApplicationViewDTO> getApplicationsByUser(Long userId) {
        List<Application> applications = applicationRepository.findByUserId(userId);
        return applications.stream()
                .map(app -> new ApplicationViewDTO(app.getId(), vacancyMapper.toDTO(app.getVacancy()), app.getStatus()))
                .collect(Collectors.toList());
    }

    public boolean hasExistingApplication(Long userId, Long vacancyId) {
        List<Application> existingApplications = applicationRepository.findListByUserIdAndVacancyId(userId, vacancyId);
        return existingApplications.stream().anyMatch(app -> app.getStatus() == ApplicationStatus.PENDING);
    }

    public Application getApplicationById(Long applicationId) { return applicationRepository.findById(applicationId).orElse(null); }

    public String getStatusInRussian(String status) {
        switch (status) {
            case "PENDING":
                return "Ожидает";
            case "ACCEPTED":
                return "Принят";
            case "REJECTED":
                return "Отклонён";
            case "WITHDRAWN":
                return "Отозван";
            default:
                return "Неизвестный статус";
        }
    }
}