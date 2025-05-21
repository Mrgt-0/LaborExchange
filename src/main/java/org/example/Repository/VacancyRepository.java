package org.example.Repository;
import org.example.Model.User;
import org.example.Model.Vacancy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VacancyRepository extends JpaRepository<Vacancy, Long> {
    List<Vacancy> findByEmployerId(Long employerId);
    List<Vacancy> findByTitleContainingIgnoreCase(String title);
    @Modifying
    @Query("DELETE FROM Vacancy v WHERE v.employer = :employer")
    void deleteByEmployer(User employer);
}
