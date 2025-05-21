package org.example.Repository;
import org.example.Model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>{
    List<Notification> findAllByUserId(Long userId);
    List<Notification> findTop10ByUserIdOrderByCreatedDateDesc(Long userId);
    void deleteByUserId(Long id);
    void deleteByVacancyId(Long vacancyId);
}
