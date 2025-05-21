package org.example.Repository;
import org.example.Model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {
    List<Resume> findByUserId(Long userId);
    Resume findResumeById(Long id);
    List<Resume> findByTitleContainingIgnoreCase(String title);
    boolean existsByUserId(Long userId);
}
