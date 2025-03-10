package kz.enu.uniAttend.repository;

import kz.enu.uniAttend.model.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {
}
