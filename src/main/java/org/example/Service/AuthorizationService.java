package org.example.Service;

import org.example.DTO.UserDTO;
import org.example.DTO.VacancyDTO;
import org.example.Model.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

//Единый сервис проверки прав доступа. Пока не используется
@Service
public class AuthorizationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthorizationService.class);

    public boolean canEditVacancy(UserDTO currentUser, VacancyDTO vacancyDTO) {
        if (currentUser == null || vacancyDTO == null) return false;
        boolean isAdmin = currentUser.getUserTypes().stream()
                .anyMatch(r -> "ROLE_ADMIN".equals(r.getTypeString()));

        Long employerId = vacancyDTO.getEmployer() != null ? vacancyDTO.getEmployer().getId() : null;
        boolean isOwner = employerId != null && employerId.equals(currentUser.getId());
        return isAdmin || isOwner;
    }
    public boolean canCreateVacancy(UserDTO user) {
        return user.getUserTypes().stream()
                .anyMatch(type -> "ROLE_EMPLOYER".equals(type.getTypeString()) || "ROLE_ADMIN".equals(type.getTypeString()));
    }
    public boolean canViewOwnVacancies(UserDTO user) {
        if (user == null) {
            logger.warn("User is null in canViewOwnVacancies");
            return false;
        }
        if (user.getUserTypes() == null || user.getUserTypes().isEmpty()) {
            logger.warn("User {} has no roles in canViewOwnVacancies", user.getEmail());
            return false;
        }

        user.getUserTypes().forEach(role -> logger.info("User {} has role: {}", user.getEmail(), role.getTypeString()));

        return user.getUserTypes().stream()
                .anyMatch(r -> {
                    String type = r.getTypeString();
                    return "ROLE_EMPLOYER".equals(type) || "ROLE_ADMIN".equals(type);
                });
    }
}
