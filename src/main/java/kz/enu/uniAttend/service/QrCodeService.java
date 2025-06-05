package kz.enu.uniAttend.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import kz.enu.uniAttend.model.entity.QRCode;
import kz.enu.uniAttend.model.entity.QRForSchedule;
import kz.enu.uniAttend.model.entity.Schedule;
import kz.enu.uniAttend.repository.QrCodeRepository;
import kz.enu.uniAttend.repository.QrForScheduleRepository;
import kz.enu.uniAttend.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QrCodeService {
    private final QrCodeRepository qrCodeRepository;
    private final QrForScheduleRepository qrForScheduleRepository;
    private final ScheduleRepository scheduleRepository;

    private static final int QR_EXPIRATION_MINUTES = 15;
    private static final int QR_SIZE = 400;

    @Transactional
    public QRCode generateQrCode(Long scheduleId) throws Exception {
        // Проверяем существование расписания
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found with ID: " + scheduleId));
        System.out.println("Generating QR code for scheduleId: " + scheduleId);

        // Формируем содержимое QR-кода
        String qrContent = String.valueOf(scheduleId);
        System.out.println("QR content: " + qrContent);

        // Настраиваем параметры QR-кода
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 4);

        BitMatrix bitMatrix = new QRCodeWriter().encode(
                qrContent,
                BarcodeFormat.QR_CODE,
                QR_SIZE,
                QR_SIZE,
                hints
        );

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        String qrCodeBase64 = Base64.getEncoder().encodeToString(outputStream.toByteArray());
        System.out.println("Generated QR code Base64 length: " + qrCodeBase64.length());

        // Сохраняем новый QR-код
        QRCode qrCode = new QRCode();
        qrCode.setQrCode(qrCodeBase64);
        qrCode.setCreatedAt(LocalDateTime.now());
        qrCode.setExpiration(LocalDateTime.now().plusMinutes(QR_EXPIRATION_MINUTES));
        qrCodeRepository.save(qrCode);
        System.out.println("Saved new QR code for scheduleId: " + scheduleId);

        // Проверка: есть ли уже связь schedule ↔ QRCode
        QRForSchedule qrForSchedule = qrForScheduleRepository.findByScheduleId(scheduleId)
                .stream()
                .findFirst()
                .orElse(null);

        if (qrForSchedule != null) {
            // Обновляем связь
            qrForSchedule.setQrCode(qrCode);
            qrForScheduleRepository.save(qrForSchedule);
            System.out.println("Updated QRForSchedule with new QRCode for scheduleId: " + scheduleId);
        } else {
            // Создаем новую связь
            QRForSchedule newQrForSchedule = new QRForSchedule();
            newQrForSchedule.setSchedule(schedule);
            newQrForSchedule.setQrCode(qrCode);
            qrForScheduleRepository.save(newQrForSchedule);
            System.out.println("Created new QRForSchedule for scheduleId: " + scheduleId);
        }

        return qrCode;
    }

    @Transactional(readOnly = true)
    public String getQrCode(Long scheduleId) throws Exception {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found with ID: " + scheduleId));
        System.out.println("Retrieving QR code for scheduleId: " + scheduleId);

        QRForSchedule qrForSchedule = qrForScheduleRepository.findByScheduleId(scheduleId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No QR code found for schedule ID: " + scheduleId));
        System.out.println("Found QRForSchedule for scheduleId: " + scheduleId);

        QRCode qrCode = qrForSchedule.getQrCode();
        if (qrCode == null || qrCode.getExpiration().isBefore(LocalDateTime.now())) {
            System.out.println("QR code not found or expired for scheduleId: " + scheduleId);
            throw new IllegalStateException("QR code not found or expired for schedule ID: " + scheduleId);
        }
        System.out.println("Returning QR code for scheduleId: " + scheduleId);
        return qrCode.getQrCode();
    }
}