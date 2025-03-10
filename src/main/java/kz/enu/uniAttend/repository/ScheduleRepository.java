package kz.enu.uniAttend.repository;

import kz.enu.uniAttend.model.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByGroupId(Long groupId);

    List<Schedule> findByLecturerId(Long lecturerId);
}
