package kz.enu.uniAttend.service;

import kz.enu.uniAttend.model.DTO.AttendanceDTO;
import kz.enu.uniAttend.model.entity.Attendance;
import kz.enu.uniAttend.model.entity.QRCode;
import kz.enu.uniAttend.model.entity.QRForSchedule;
import kz.enu.uniAttend.model.request.ScanRequest;
import kz.enu.uniAttend.repository.AttendanceRepository;
import kz.enu.uniAttend.repository.QrCodeRepository;
import kz.enu.uniAttend.repository.QrForScheduleRepository;
import kz.enu.uniAttend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final QrCodeRepository qrCodeRepository;
    private final QrForScheduleRepository qrForScheduleRepository;
    private final UserRepository userRepository;

    // Координаты университета (пример для Астаны)
    private static final double UNIVERSITY_LAT = 51.1605;
    private static final double UNIVERSITY_LON = 71.4704;
    private static final double MAX_DISTANCE_KM = 0.5; // 500 метров


    public AttendanceDTO scanQrCode(ScanRequest request, Long userId) throws Exception {
        // Проверка существования QR-кода
        QRCode qrCode = qrCodeRepository.findById(request.getQrCodeId())
                .orElseThrow(() -> new Exception("QR code not found"));

        // Проверка срока действия QR-кода
        if (qrCode.getExpiration().isBefore(LocalDateTime.now())) {
            throw new Exception("QR code expired");
        }

        // Проверка геолокации
        if (!isWithinUniversityArea(request.getLatitude(), request.getLongitude())) {
            throw new Exception("You are not within university area");
        }

        // Получение scheduleId из связи QR-кода
        QRForSchedule qrForSchedule = qrForScheduleRepository.findByQrCodeId(request.getQrCodeId())
                .orElseThrow(() -> new Exception("Schedule not found for this QR"));

        // Проверка существования пользователя
        userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        // Сохранение записи посещаемости
        Attendance attendance = new Attendance();
        attendance.setUser(userRepository.findById(userId).orElseThrow(() -> new Exception("User not found")));
        attendance.setSchedule(qrForSchedule.getSchedule());
        attendance.setScanTime(LocalDateTime.now());
        attendance.setScanType(Attendance.ScanType.valueOf(request.getScanType()));
        attendanceRepository.save(attendance);

        // Расчет и возврат результата
        return calculateAttendance(userId, qrForSchedule.getSchedule().getId());
    }

    private boolean isWithinUniversityArea(double lat, double lon) {
        double distance = calculateDistance(lat, lon, UNIVERSITY_LAT, UNIVERSITY_LON);
        return distance <= MAX_DISTANCE_KM;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Формула Haversine для расчета расстояния между двумя точками
        double R = 6371; // Радиус Земли в км
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }

    private AttendanceDTO calculateAttendance(Long userId, Long scheduleId) {
        List<Attendance> attendances = attendanceRepository
                .findByUserIdAndScheduleIdOrderByScanTimeAsc(userId, scheduleId);

        AttendanceDTO response = new AttendanceDTO();
        response.setUserId(userId);
        response.setScheduleId(scheduleId);

        Optional<Attendance> inScan = attendances.stream()
                .filter(a -> "IN".equals(a.getScanType()))
                .findFirst();

        Optional<Attendance> outScan = attendances.stream()
                .filter(a -> "OUT".equals(a.getScanType()))
                .findFirst();

        if (inScan.isPresent()) {
            response.setScanInTime(inScan.get().getScanTime());
        }

        if (outScan.isPresent()) {
            response.setScanOutTime(outScan.get().getScanTime());
        }

        if (inScan.isPresent() && outScan.isPresent()) {
            long minutes = ChronoUnit.MINUTES.between(
                    inScan.get().getScanTime(),
                    outScan.get().getScanTime()
            );
            response.setMinutesPresent(minutes > 0 ? minutes : 0);
        }

        return response;
    }
}




//__---------------------------------------------------- Мнау обьяснение -----------------------------------------
//Полное объяснение:
//Назначение сервиса:
//
//AttendanceService отвечает за обработку сканирования QR-кодов студентами, проверку геолокации и подсчет времени присутствия на занятии. Он взаимодействует с базой данных через репозитории и предоставляет функциональность для регистрации входа (IN) и выхода (OUT) студентов.
//Основные компоненты:
//
//Поля и зависимости:
//Репозитории (attendanceRepository, qrCodeRepository, и т.д.) используются для доступа к данным.
//        Константы UNIVERSITY_LAT, UNIVERSITY_LON, и MAX_DISTANCE_KM задают координаты университета и допустимый радиус присутствия.
//Конструктор использует внедрение зависимостей через Spring (@Autowired).
//Метод scanQrCode:
//Это основной публичный метод, который вызывается при сканировании QR-кода.
//        Принимает ScanRequest (содержит ID QR-кода, координаты и тип сканирования) и userId.
//Выполняет последовательность проверок и операций:
//Проверяет существование QR-кода.
//Проверяет срок действия QR-кода.
//        Проверяет, находится ли студент в пределах университета.
//Находит связь между QR-кодом и расписанием.
//Проверяет существование пользователя.
//Сохраняет запись посещаемости.
//Рассчитывает и возвращает результат.
//Метод isWithinUniversityArea:
//Вспомогательный приватный метод для проверки геолокации.
//Вычисляет расстояние до университета и сравнивает его с максимальным допустимым значением.
//Метод calculateDistance:
//Реализует формулу Haversine для точного расчета расстояния между двумя точками на поверхности Земли.
//Использует широту и долготу в радианах для вычислений.
//Метод calculateAttendance:
//Рассчитывает время присутствия на основе записей входа и выхода.
//Ищет первую запись IN и первую запись OUT для заданного пользователя и расписания.
//Вычисляет разницу во времени в минутах, если обе записи присутствуют.
//
//        Особенности:
//
//Обработка ошибок: Метод выбрасывает исключения с понятными сообщениями при любых проблемах (например, истекший QR-код или неверная геолокация).
//Триггер базы данных: Логика сервиса полагается на триггер attendance_order_trigger из вашей схемы для проверки порядка IN/OUT.
//        Гибкость: Константы геолокации можно легко изменить для другого университета.
//Точность: Использование формулы Haversine обеспечивает точный расчет расстояния с учетом сферической формы Земли.
