package kz.enu.uniAttend.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import kz.enu.uniAttend.model.entity.*;
import kz.enu.uniAttend.repository.QrCodeRepository;
import kz.enu.uniAttend.repository.QrForScheduleRepository;
import kz.enu.uniAttend.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Добавляем импорт

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QrCodeService {
    private final QrCodeRepository qrCodeRepository;
    private final QrForScheduleRepository qrForScheduleRepository;
    private final ScheduleRepository scheduleRepository;

    private static final int QR_EXPIRATION_MINUTES = 15;

    @Transactional // Добавляем аннотацию
    public QRCode generateQrCode(Long scheduleId) throws Exception {
        String qrContent = "schedule:" + scheduleId + ":" + UUID.randomUUID();
        BitMatrix bitMatrix = new QRCodeWriter().encode(
                qrContent,
                BarcodeFormat.QR_CODE,
                400,
                400
        );

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        String qrCodeBase64 = Base64.getEncoder().encodeToString(pngOutputStream.toByteArray());

        qrCodeRepository.deleteByScheduleId(scheduleId);

        QRCode qrCode = new QRCode();
        qrCode.setQrCode(qrCodeBase64);
        qrCode.setCreatedAt(LocalDateTime.now());
        qrCode.setExpiration(LocalDateTime.now().plusMinutes(QR_EXPIRATION_MINUTES));
        qrCodeRepository.save(qrCode);

        QRForSchedule qrForSchedule = new QRForSchedule();
        qrForSchedule.setSchedule(scheduleRepository.findById(scheduleId).orElseThrow(() -> new RuntimeException("Schedule not found")));
        qrForSchedule.setQrCode(qrCodeRepository.findById(qrCode.getId()).orElseThrow(() -> new RuntimeException("QR not found")));
        qrForScheduleRepository.save(qrForSchedule);

        return qrCode;
    }

    public String getQrCode(Long scheduleId) throws Exception {
        scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new Exception("Schedule not found"));

        QRForSchedule qrForSchedule = qrForScheduleRepository.findByScheduleId(scheduleId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new Exception("No QR code found for this schedule"));

        QRCode qrCode = qrForSchedule.getQrCode();

        if (qrCode.getExpiration().isBefore(LocalDateTime.now())) {
            throw new Exception("QR code has expired");
        }

        return qrCode.getQrCode();
    }
}