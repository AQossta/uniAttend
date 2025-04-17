package kz.enu.uniAttend.repository;

import kz.enu.uniAttend.model.entity.QRCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QrCodeRepository extends JpaRepository<QRCode, Long> {
    @Modifying
    @Query("DELETE FROM QRCode q WHERE q.id IN (SELECT qfs.qrCode.id FROM QRForSchedule qfs WHERE qfs.schedule.id = :scheduleId)")
    void deleteByScheduleId(@Param("scheduleId") Long scheduleId);
}
