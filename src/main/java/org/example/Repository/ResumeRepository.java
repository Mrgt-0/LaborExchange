package org.example.Repository;

import org.example.Model.Resume;
import org.example.Model.Vacancy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {
    List<Resume> findByUserId(Long userId);
    Optional<Resume> findById(Long id);
    List<Resume> findByTitleContainingIgnoreCase(String title);
}
