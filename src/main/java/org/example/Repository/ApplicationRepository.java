package org.example.Repository;

import org.example.Model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Optional<Application> findByUserIdAndVacancyId(Long userId, Long vacancyId);
    List<Application> findByUserId(Long userId);
    List<Application> findByVacancyId(Long vacancyId);
}
