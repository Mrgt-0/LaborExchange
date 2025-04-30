package org.example.Repository;

import org.example.Enum.UserTypeEnum;
import org.example.Model.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTypeRepository extends JpaRepository<UserType, Long> {
    UserType findByType(UserTypeEnum type);
}
