package org.example.Repository;
import org.example.Model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    @Query("SELECT a FROM Application a WHERE a.userId = :userId AND a.vacancy.id = :vacancyId")
    List<Application> findListByUserIdAndVacancyId(@Param("userId") Long userId, @Param("vacancyId") Long vacancyId);
    List<Application> findByUserId(Long userId);
    List<Application> findByVacancyId(Long vacancyId);
    void deleteByVacancyId(Long id);
    void deleteByUserId(Long userId);
}
