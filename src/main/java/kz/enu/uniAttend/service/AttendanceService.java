package kz.enu.uniAttend.service;

import kz.enu.uniAttend.model.DTO.AttendanceDTO;
import kz.enu.uniAttend.model.entity.*;
import kz.enu.uniAttend.model.request.ScanRequest;
import kz.enu.uniAttend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final JournalRepository journalRepository;

//    private static final double UNIVERSITY_LAT = 51.15980899270086;
//    private static final double UNIVERSITY_LON = 71.46491875786816;

    private static final double UNIVERSITY_LAT = 43.223308537891185;
    private static final double UNIVERSITY_LON = 76.85859152407674;
    private static final double MAX_DISTANCE_KM = 0.5;

    @Transactional
    public AttendanceDTO processAttendance(ScanRequest request) {
        validateRequest(request);
        User user = getUser(request.getUserId());
        Long scheduleId = validateScheduleId(request.getScheduleId());
        Schedule schedule = getSchedule(scheduleId);

        validateLocation(request.getLatitude(), request.getLongitude());
        checkDuplicateScan(request.getUserId(), schedule.getId(), request.getScanType());
        Attendance attendance = createAttendance(user, schedule, request.getScanType());
        attendanceRepository.save(attendance);
        if (request.getScanType() == "OUT") {
            addJournal(request.getUserId(), request.getScheduleId());
        }
        log.info("Attendance recorded for user {} at schedule {}", request.getUserId(), schedule.getId());
        AttendanceDTO stats = calculateAttendanceStats(request.getUserId(), schedule.getId());
        return stats;
    }

    private void addJournal(Long userId, Long scheduleId) {
        if (journalRepository.existsByUserIdAndScheduleId(userId, scheduleId)) {
            Journal journal = journalRepository.findByUserIdAndScheduleId(userId, scheduleId);
            journal.setAssessment("0");
            journalRepository.save(journal);
        } else {
            User user = getUser(userId);
            Schedule schedule = getSchedule(scheduleId);
            String assessment = "0";
            LocalDateTime dateCreate = LocalDateTime.now();

            Journal journal = new Journal(user, schedule, assessment, dateCreate);
            journalRepository.save(journal);
        }
    }

    private void validateRequest(ScanRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Scan request cannot be null");
        }
        if (request.getUserId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID is required");
        }
        if (request.getScheduleId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Schedule ID is required");
        }
        if (request.getScanType() == null || request.getScanType().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Scan type is required");
        }
        try {
            Attendance.ScanType.valueOf(request.getScanType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid scan type: " + request.getScanType());
        }
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + userId));
    }

    private Schedule getSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Schedule not found with ID: " + scheduleId));
    }

    private Long validateScheduleId(Long scheduleId) {
        if (scheduleId == null || scheduleId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid schedule ID");
        }
        return scheduleId;
    }

    private void validateLocation(Double latitude, Double longitude) {
        if (latitude == null || longitude == null || latitude == 0.0 || longitude == 0.0) {
            log.warn("Invalid location data: latitude={}, longitude={}", latitude, longitude);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valid location data is required");
        }

        if (!isWithinUniversityArea(latitude, longitude)) {
            log.info("Location check failed: latitude={}, longitude={}, distance={} km",
                    latitude, longitude, calculateDistance(latitude, longitude, UNIVERSITY_LAT, UNIVERSITY_LON));
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are not within university area");
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
                userId, scheduleId, Attendance.ScanType.valueOf(scanType.toUpperCase()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have already scanned " + scanType + " for this class");
        }
    }

    private Attendance createAttendance(User user, Schedule schedule, String scanType) {
        Attendance attendance = new Attendance();
        attendance.setUser(user);
        attendance.setSchedule(schedule);
        attendance.setScanTime(LocalDateTime.now());
        attendance.setScanType(Attendance.ScanType.valueOf(scanType.toUpperCase()));
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