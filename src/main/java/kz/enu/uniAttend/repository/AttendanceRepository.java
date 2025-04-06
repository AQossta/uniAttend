package kz.enu.uniAttend.repository;

import kz.enu.uniAttend.model.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByUserIdAndScheduleIdOrderByScanTimeAsc(Long userId, Long scheduleId);

    boolean existsByUserIdAndScheduleIdAndScanType(Long userId, Long scheduleId, Attendance.ScanType scanType);

    List<Attendance> findByScheduleId(Long scheduleId);
}
