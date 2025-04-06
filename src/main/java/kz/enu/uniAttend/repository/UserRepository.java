package kz.enu.uniAttend.repository;


import kz.enu.uniAttend.model.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
     boolean existsByUserName(String userName);
     Optional<User> findByUserName(String userName);
     boolean existsByEmail(String email);

     Optional<User> findByEmail(String email);

    double countByGroupId(Long groupId);
}
