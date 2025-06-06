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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class QrCodeService {
    private final QrCodeRepository qrCodeRepository;
    private final QrForScheduleRepository qrForScheduleRepository;
    private final ScheduleRepository scheduleRepository;

    private static final int QR_EXPIRATION_MINUTES = 15;
    private static final int QR_SIZE = 400;
    private static final int MAX_QR_FOR_SCHEDULES = 3;

    // Реестр блокировок для scheduleId
    private final Map<Long, Object> locks = new ConcurrentHashMap<>();

    private Object getLock(Long scheduleId) {
        return locks.computeIfAbsent(scheduleId, k -> new Object());
    }

    @Transactional
    public QRCode generateQrCode(Long scheduleId) throws Exception {
        synchronized (getLock(scheduleId)) { // Синхронизация по scheduleId
            try {
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

                // Получаем существующие связи для scheduleId
                List<QRForSchedule> existingQrForSchedules = qrForScheduleRepository.findAllByScheduleId(scheduleId);
                System.out.println("Found " + existingQrForSchedules.size() + " existing QRForSchedule entries for scheduleId: " + scheduleId);

                // Если связей больше или равно 3, удаляем самую старую
                if (existingQrForSchedules.size() >= MAX_QR_FOR_SCHEDULES) {
                    QRForSchedule oldest = existingQrForSchedules.stream()
                            .min(Comparator.comparing(qr -> qr.getQrCode().getCreatedAt()))
                            .orElseThrow(() -> new IllegalStateException("Error finding oldest QRForSchedule"));
                    qrForScheduleRepository.delete(oldest);
                    qrCodeRepository.delete(oldest.getQrCode());
                    System.out.println("Deleted oldest QRForSchedule and QRCode for scheduleId: " + scheduleId);
                }

                // Создаем новую связь
                QRForSchedule newQrForSchedule = new QRForSchedule();
                newQrForSchedule.setSchedule(schedule);
                newQrForSchedule.setQrCode(qrCode);
                qrForScheduleRepository.save(newQrForSchedule);
                System.out.println("Created new QRForSchedule for scheduleId: " + scheduleId);

                return qrCode;
            } catch (Exception e) {
                System.err.println("Error generating QR code for scheduleId: " + scheduleId + ", error: " + e.getMessage());
                throw e;
            }
        }
    }

    @Transactional(readOnly = true)
    public String getQrCode(Long scheduleId) throws Exception {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found with ID: " + scheduleId));
        System.out.println("Retrieving QR code for scheduleId: " + scheduleId);

        List<QRForSchedule> qrForSchedules = qrForScheduleRepository.findAllByScheduleId(scheduleId);
        if (qrForSchedules.isEmpty()) {
            throw new IllegalArgumentException("No QR code found for schedule ID: " + scheduleId);
        }

        // Находим самый последний действующий QR-код
        QRForSchedule latestQrForSchedule = qrForSchedules.stream()
                .filter(qr -> qr.getQrCode() != null && !qr.getQrCode().getExpiration().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(qr -> qr.getQrCode().getCreatedAt()))
                .orElseThrow(() -> new IllegalStateException("No valid QR code found for schedule ID: " + scheduleId));

        System.out.println("Found QRForSchedule for scheduleId: " + scheduleId);
        QRCode qrCode = latestQrForSchedule.getQrCode();
        System.out.println("Returning QR code for scheduleId: " + scheduleId);
        return qrCode.getQrCode();
    }
}