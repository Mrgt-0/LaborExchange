package org.example.Service;

import org.example.Enum.ApplicationStatus;
import org.example.Model.Application;
import org.example.Repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ApplicationService {
//    private final ApplicationRepository applicationRepository;
//
//    @Autowired
//    public ApplicationService(ApplicationRepository applicationRepository) {
//        this.applicationRepository = applicationRepository;
//    }
//
//    // Метод для подачи отклика на вакансию
//    public Application submitApplication(Long userId, Long vacancyId) {
//        Application application = new Application(userId, vacancyId, ApplicationStatus.PENDING);
//        return applicationRepository.save(application);
//    }
//
//    // Метод для отзыва отклика
//    public Optional<Application> withdrawApplication(Long applicationId) {
//        Optional<Application> application = applicationRepository.findById(applicationId);
//        if (application.isPresent()) {
//            application.get().withdraw(); // Отзываем отклик
//            return Optional.of(applicationRepository.save(application.get())); // Сохраняем изменения
//        }
//        return Optional.empty();
//    }
//
//    // Метод для обновления статуса отклика
//    public Optional<Application> updateApplicationStatus(Long applicationId, ApplicationStatus newStatus) {
//        Optional<Application> applicationOpt = applicationRepository.findById(applicationId);
//        if (applicationOpt.isPresent()) {
//            Application application = applicationOpt.get();
//            application.updateStatus(newStatus);  // Обновляем статус
//            return Optional.of(applicationRepository.save(application)); // Сохраняем изменения
//        }
//        return Optional.empty();
//    }
//
//    // Метод для получения всех откликов
//    public List<Application> getAllApplications() {
//        return applicationRepository.findAll();
//    }
//
//    // Метод для получения отклика по его ID
//    public Optional<Application> getApplicationById(Long applicationId) {
//        return applicationRepository.findById(applicationId);
//    }
}
