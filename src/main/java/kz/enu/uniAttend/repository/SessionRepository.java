package kz.enu.uniAttend.repository;

import kz.enu.uniAttend.model.entity.Session;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends CrudRepository<Session, Long> {
     int deleteByToken(String token);
     List<Session> findByUserId(Long userId);

     Optional<Session> findByToken(String token);
}
