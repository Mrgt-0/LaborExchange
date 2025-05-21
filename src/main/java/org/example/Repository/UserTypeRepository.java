package org.example.Repository;
import org.example.Enum.UserTypeEnum;
import org.example.Model.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Collection;
import java.util.List;

@Repository
public interface UserTypeRepository extends JpaRepository<UserType, Long> {
    UserType findByType(UserTypeEnum type);
    List<UserType> findByTypeIn(Collection<String> types);
}
