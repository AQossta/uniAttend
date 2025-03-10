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


    public String generateQrCode(Long scheduleId) throws Exception {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new Exception("Schedule not found"));

        String qrContent = "schedule:" + scheduleId + ":" + UUID.randomUUID().toString();
        BitMatrix bitMatrix = new QRCodeWriter().encode(
                qrContent,
                BarcodeFormat.QR_CODE,
                200,
                200
        );

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        String qrCodeBase64 = Base64.getEncoder().encodeToString(pngOutputStream.toByteArray());

        QRCode qrCode = new QRCode();
        qrCode.setQrCode(qrCodeBase64);
        qrCode.setCreatedAt(LocalDateTime.now());
        qrCode.setExpiration(LocalDateTime.now().plusMinutes(QR_EXPIRATION_MINUTES));
        qrCodeRepository.save(qrCode);

        QRForSchedule qrForSchedule = new QRForSchedule();
        qrForSchedule.setSchedule(scheduleRepository.findById(scheduleId).orElseThrow(() -> new RuntimeException("Schedule not found")));
        qrForSchedule.setQrCode(qrCodeRepository.findById(qrCode.getId()).orElseThrow(() -> new RuntimeException("QR not found")));
        qrForScheduleRepository.save(qrForSchedule);

        return qrCodeBase64;
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



//----------------------------------------------------------------------------------------------------
//Полное объяснение:
//Назначение сервиса:
//
//QrCodeService отвечает за создание QR-кодов для уроков (расписаний), которые затем могут быть отсканированы студентами для отметки посещаемости. Сервис генерирует изображение QR-кода, сохраняет его в базе данных и связывает с конкретным занятием.
//Основные компоненты:
//
//Поля и зависимости:
//Репозитории (qrCodeRepository, qrForScheduleRepository, scheduleRepository) используются для взаимодействия с соответствующими таблицами базы данных (t_qr_codes, t_qr_for_schedules, t_schedules).
//Константа QR_EXPIRATION_MINUTES задает срок действия QR-кода (15 минут).
//Конструктор использует внедрение зависимостей через Spring (@Autowired), что позволяет легко подменять реализации репозиториев (например, для тестов).
//Метод generateQrCode:
//Это единственный публичный метод сервиса, который выполняет всю логику генерации QR-кода.
//Принимает scheduleId - идентификатор расписания, для которого создается QR-код.
//Возвращает строку Base64, представляющую изображение QR-кода.
//Выполняет следующие шаги:
//Проверка расписания: Убеждается, что указанное расписание существует.
//Генерация контента: Создает уникальную строку для кодирования в QR-код.
//Создание изображения: Использует ZXing для генерации QR-кода размером 200x200 пикселей.
//Преобразование в Base64: Конвертирует изображение в строку для передачи.
//Сохранение в базе: Сохраняет QR-код с метаданными (время создания и истечения).
//Связывание с расписанием: Создает запись в таблице связи.
//Возврат результата: Отправляет Base64-строку вызывающей стороне.
//
//        Особенности:
//
//Уникальность: Использование UUID.randomUUID() обеспечивает уникальность каждого QR-кода, даже для одного и того же расписания.
//Срок действия: QR-код действителен только 15 минут, что соответствует значению QR_EXPIRATION_MINUTES.
//Формат: QR-код возвращается как Base64-строка, что удобно для передачи через API и отображения в веб-интерфейсе.
//        Безопасность: Проверка существования расписания предотвращает генерацию QR-кодов для несуществующих занятий.
//Соответствие схеме: Сервис полностью использует вашу структуру базы данных (t_qr_codes и t_qr_for_schedules).
//
//Зависимости:
//
//ZXing: Библиотека для генерации QR-кодов (нужны зависимости com.google.zxing:core и com.google.zxing:javase в pom.xml).
//Spring Data JPA: Для работы с репозиториями.
