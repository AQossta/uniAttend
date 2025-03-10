package kz.enu.uniAttend.repository;

import kz.enu.uniAttend.model.entity.QRCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QrCodeRepository extends JpaRepository<QRCode, Long> {
}
