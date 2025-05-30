package kz.enu.uniAttend.service;
import kz.enu.uniAttend.model.DTO.AttendanceDTO;
import kz.enu.uniAttend.model.DTO.ScheduleDTO;
import kz.enu.uniAttend.model.DTO.StatisticsPanelDTO;
import kz.enu.uniAttend.model.DTO.StudentDTO;
import kz.enu.uniAttend.model.entity.Attendance;
import kz.enu.uniAttend.model.entity.Role;
import kz.enu.uniAttend.model.entity.User;
import kz.enu.uniAttend.repository.AttendanceRepository;
import kz.enu.uniAttend.repository.ScheduleRepository;
import kz.enu.uniAttend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsPanelService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final ScheduleService scheduleService;
    private final RoleService roleService;


    public StatisticsPanelDTO getAttendanceStatistics(Long scheduleId) {
        ScheduleDTO scheduleDTO = scheduleService.getScheduleById(scheduleId);
        List<Attendance> attendances = attendanceRepository.findByScheduleId(scheduleDTO.getId());
        Long groupId = scheduleDTO.getGroupId();

        // Получаем всех пользователей в группе
        List<User> users = userRepository.findByGroupId(groupId);

        // Фильтруем только студентов
        List<User> students = users.stream()
                .filter(user -> {
                    List<Role> roles = roleService.getAllForUserId(user.getId());
                    return roles.stream().anyMatch(role -> "student".equalsIgnoreCase(role.getName()));
                })
                .collect(Collectors.toList());

        // Считаем общее количество студентов
        Double totalCount = (double) students.size();

        // Считаем количество присутствующих студентов
        Double presentCount = (double) attendances.stream()
                .filter(att -> att.getScanType() == Attendance.ScanType.IN)
                .map(Attendance::getUser)
                .distinct()
                .filter(user -> {
                    List<Role> roles = roleService.getAllForUserId(user.getId());
                    return roles.stream().anyMatch(role -> "student".equalsIgnoreCase(role.getName()));
                })
                .count();

        Double statistic = totalCount > 0 ? (presentCount / totalCount) * 100 : 0.0;

        // Получаем время окончания занятия
        LocalDateTime endTime = scheduleDTO.getEndTime();

        // Создаем список StudentDTO только для студентов
        List<StudentDTO> studentDTOs = students.stream().map(user -> {
            StudentDTO studentDTO = new StudentDTO();
            studentDTO.setId(user.getId());
            studentDTO.setName(user.getUserName());
            studentDTO.setEmail(user.getEmail());
            studentDTO.setPhoneNumber(user.getPhoneNumber());

            // Проверяем, есть ли запись посещения с ScanType.IN
            boolean attended = attendances.stream()
                    .anyMatch(att -> att.getScanType() == Attendance.ScanType.IN
                            && att.getUser().getId().equals(user.getId()));
            studentDTO.setAttend(attended);

            // Рассчитываем время участия
            if (attended) {
                // Находим запись IN
                Attendance inRecord = attendances.stream()
                        .filter(att -> att.getScanType() == Attendance.ScanType.IN
                                && att.getUser().getId().equals(user.getId()))
                        .findFirst()
                        .orElse(null);

                // Находим запись OUT
                Attendance outRecord = attendances.stream()
                        .filter(att -> att.getScanType() == Attendance.ScanType.OUT
                                && att.getUser().getId().equals(user.getId()))
                        .findFirst()
                        .orElse(null);

                if (inRecord != null && inRecord.getScanTime() != null) {
                    studentDTO.setAttendTime(inRecord.getScanTime());
                    LocalDateTime endTimeForCalculation = outRecord != null && outRecord.getScanTime() != null
                            ? outRecord.getScanTime()
                            : endTime; // Если OUT отсутствует, используем endTime занятия

                    if (endTimeForCalculation != null) {
                        // Рассчитываем время участия в минутах
                        long durationMinutes = ChronoUnit.MINUTES.between(
                                inRecord.getScanTime(), endTimeForCalculation
                        );
                        studentDTO.setAttendanceDuration(durationMinutes);
                        studentDTO.setExitTime(outRecord != null ? outRecord.getScanTime() : null);
                    } else {
                        studentDTO.setAttendanceDuration(0);
                        studentDTO.setExitTime(null);
                    }
                } else {
                    studentDTO.setAttendanceDuration(0);
                    studentDTO.setExitTime(null);
                }
            } else {
                studentDTO.setAttendTime(null);
                studentDTO.setExitTime(null);
                studentDTO.setAttendanceDuration(0);
            }

            return studentDTO;
        }).collect(Collectors.toList());

        StatisticsPanelDTO stats = new StatisticsPanelDTO();
        stats.setScheduleDTO(scheduleDTO);
        stats.setTotalCount(totalCount);
        stats.setPresentCount(presentCount);
        stats.setStatistic(statistic);
        stats.setStudentDTO(studentDTOs);

        return stats;
    }
}


