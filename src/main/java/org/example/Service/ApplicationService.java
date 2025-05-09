package org.example.Service;

import org.example.DTO.ApplicationDTO;
import org.example.DTO.ApplicationViewDTO;
import org.example.Enum.ApplicationStatus;
import org.example.Mapper.ApplicationMapper;
import org.example.Model.Application;
import org.example.Model.Vacancy;
import org.example.Repository.ApplicationRepository;
import org.example.Repository.VacancyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ApplicationService {
    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ApplicationMapper applicationMapper;

    @Autowired
    private VacancyRepository vacancyRepository;
    // Подать новый отклик
    public ApplicationDTO submitApplication(Long userId, Long vacancyId) {
        // Проверка наличия уже отклика от того же пользователя на эту вакансию
        Optional<Application> existing = applicationRepository.findByUserIdAndVacancyId(userId, vacancyId);
        if (existing.isPresent()) {
            throw new IllegalStateException("Вы уже подали отклик на эту вакансию");
        }
        Application application = new Application(userId, vacancyId);
        application.submit();
        return applicationMapper.toDTO(applicationRepository.save(application));
    }

    // Отозвать отклик
    public Application withdrawApplication(Long applicationId, Long userId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Отклик не найден"));

        if (!application.getUserId().equals(userId)) {
            throw new SecurityException("Недостаточно прав");
        }

        application.withdraw();
        return applicationRepository.save(application);
    }
    // Обновление статуса отклика\
    public Application updateStatus(Long applicationId, ApplicationStatus newStatus) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Отклик не найден"));

        application.setStatus(newStatus);
        return applicationRepository.save(application);
    }

    // Получение откликов по пользователю
    public List<Application> getApplicationsByUser(Long userId) {
        return applicationRepository.findByUserId(userId);
    }

    // Получение откликов по вакансии
    public List<Application> getApplicationsByVacancy(Long vacancyId) {
        return applicationRepository.findByVacancyId(vacancyId);
    }

    public List<ApplicationViewDTO> getApplicationsForUserWithVacancyTitle(Long userId) {
        List<Application> applications = applicationRepository.findByUserId(userId);
        List<ApplicationViewDTO> result = new ArrayList<>();

        for (Application app : applications) {
            Vacancy vacancy = vacancyRepository.findById(app.getVacancyId()).orElse(null);
            String title = vacancy != null ? vacancy.getTitle() : "Неизвестно";

            ApplicationViewDTO dto = new ApplicationViewDTO(app.getId(), app.getStatus(), title);
            result.add(dto);
        }
        return result;
    }
}
