package kz.enu.uniAttend.service;

import kz.enu.uniAttend.exception.*;
import kz.enu.uniAttend.model.DTO.AttendanceDTO;
import kz.enu.uniAttend.model.entity.*;
import kz.enu.uniAttend.model.request.ScanRequest;
import kz.enu.uniAttend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final QrCodeRepository qrCodeRepository;
    private final QrForScheduleRepository qrForScheduleRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;

    private static final double UNIVERSITY_LAT = 51.1605;
    private static final double UNIVERSITY_LON = 71.4704;
    private static final double MAX_DISTANCE_KM = 0.5;

    @Transactional
    public AttendanceDTO processAttendance(ScanRequest request) {
        // 1. Валидация входных данных
        validateRequest(request);

        // 2. Получение и проверка сущностей
        User user = getUser(request.getUserId());
        Schedule schedule = getSchedule(request.getScheduleId());
        QRCode qrCode = validateActiveQrCodeForSchedule(schedule.getId());

        // 3. Проверка геолокации
        validateLocation(request.getLatitude(), request.getLongitude());

        // 4. Проверка дублирования сканирования
        checkDuplicateScan(request.getUserId(), schedule.getId(), request.getScanType());

        // 5. Сохранение посещаемости
        Attendance attendance = createAttendance(user, schedule, request.getScanType());
        attendanceRepository.save(attendance);

        log.info("Attendance recorded for user {} at schedule {}", request.getUserId(), schedule.getId());

        return calculateAttendanceStats(request.getUserId(), schedule.getId());
    }

    private void validateRequest(ScanRequest request) {
        if (request == null) {
            throw new RuntimeException("Scan request cannot be null");
        }
        if (request.getScheduleId() == null) {
            throw new RuntimeException("Schedule ID is required");
        }
        if (request.getScanType() == null ||
                (!request.getScanType().equals("IN") && !request.getScanType().equals("OUT"))) {
            throw new RuntimeException("Invalid scan type");
        }
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }

    private Schedule getSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found with ID: " + scheduleId));
    }

    private QRCode validateActiveQrCodeForSchedule(Long scheduleId) {
        QRForSchedule qrForSchedule = qrForScheduleRepository
                .findFirstByScheduleIdOrderByQrCodeCreatedAtDesc(scheduleId)
                .orElseThrow(() -> new RuntimeException("No QR code found for schedule"));

        QRCode qrCode = qrForSchedule.getQrCode();

        if (qrCode.getExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("QR code has expired");
        }

        return qrCode;
    }

    private void validateLocation(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            throw new RuntimeException("Location data is required");
        }

        if (!isWithinUniversityArea(latitude, longitude)) {
            throw new RuntimeException("You are not within university area");
        }
    }

    private boolean isWithinUniversityArea(double lat, double lon) {
        double distance = calculateDistance(lat, lon, UNIVERSITY_LAT, UNIVERSITY_LON);
        return distance <= MAX_DISTANCE_KM;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }

    private void checkDuplicateScan(Long userId, Long scheduleId, String scanType) {
        if (attendanceRepository.existsByUserIdAndScheduleIdAndScanType(
                userId, scheduleId, Attendance.ScanType.valueOf(scanType))) {
            throw new RuntimeException("You have already scanned " + scanType + " for this class");
        }
    }

    private Attendance createAttendance(User user, Schedule schedule, String scanType) {
        Attendance attendance = new Attendance();
        attendance.setUser(user);
        attendance.setSchedule(schedule);
        attendance.setScanTime(LocalDateTime.now());
        attendance.setScanType(Attendance.ScanType.valueOf(scanType));
        return attendance;
    }

    private AttendanceDTO calculateAttendanceStats(Long userId, Long scheduleId) {
        List<Attendance> attendances = attendanceRepository
                .findByUserIdAndScheduleIdOrderByScanTimeAsc(userId, scheduleId);

        AttendanceDTO response = new AttendanceDTO();
        response.setUserId(userId);
        response.setScheduleId(scheduleId);

        Optional<Attendance> inScan = findFirstScanByType(attendances, Attendance.ScanType.IN);
        Optional<Attendance> outScan = findFirstScanByType(attendances, Attendance.ScanType.OUT);

        inScan.ifPresent(att -> response.setScanInTime(att.getScanTime()));
        outScan.ifPresent(att -> response.setScanOutTime(att.getScanTime()));

        if (inScan.isPresent() && outScan.isPresent()) {
            long minutes = ChronoUnit.MINUTES.between(
                    inScan.get().getScanTime(),
                    outScan.get().getScanTime()
            );
            response.setMinutesPresent(Math.max(minutes, 0));
        }

        return response;
    }

    private Optional<Attendance> findFirstScanByType(List<Attendance> attendances, Attendance.ScanType type) {
        return attendances.stream()
                .filter(a -> type.equals(a.getScanType()))
                .findFirst();
    }
}