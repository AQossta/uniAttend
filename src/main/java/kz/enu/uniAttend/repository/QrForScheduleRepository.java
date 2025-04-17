package kz.enu.uniAttend.repository;

import kz.enu.uniAttend.model.entity.QRForSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.Optional;

public interface QrForScheduleRepository extends JpaRepository<QRForSchedule, Long> {
    Optional<QRForSchedule> findByQrCodeId(Long qrCodeId);

    Optional<QRForSchedule> findByScheduleId(Long scheduleId);

    Optional<QRForSchedule> findFirstByScheduleIdOrderByQrCodeCreatedAtDesc(Long scheduleId);

    void deleteByScheduleId(Long scheduleId);
}
