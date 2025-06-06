package kz.enu.uniAttend.repository;

import kz.enu.uniAttend.model.entity.QRForSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
@Repository
public interface QrForScheduleRepository extends JpaRepository<QRForSchedule, Long> {
    Optional<QRForSchedule> findByQrCodeId(Long qrCodeId);

    List<QRForSchedule> findAllByScheduleId(Long scheduleId);

    Optional<QRForSchedule> findFirstByScheduleIdOrderByQrCodeCreatedAtDesc(Long scheduleId);

    void deleteByScheduleId(Long scheduleId);
}
