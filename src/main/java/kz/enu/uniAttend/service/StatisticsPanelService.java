package kz.enu.uniAttend.service;
import kz.enu.uniAttend.model.DTO.AttendanceDTO;
import kz.enu.uniAttend.model.DTO.ScheduleDTO;
import kz.enu.uniAttend.model.DTO.StatisticsPanelDTO;
import kz.enu.uniAttend.model.DTO.StudentDTO;
import kz.enu.uniAttend.model.entity.Attendance;
import kz.enu.uniAttend.model.entity.User;
import kz.enu.uniAttend.repository.AttendanceRepository;
import kz.enu.uniAttend.repository.ScheduleRepository;
import kz.enu.uniAttend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsPanelService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final ScheduleService scheduleService;

    public StatisticsPanelDTO getAttendanceStatistics(Long scheduleId) {
        ScheduleDTO scheduleDTO = scheduleService.getScheduleById(scheduleId);
        List<Attendance> attendances = attendanceRepository
                .findByScheduleId(scheduleDTO.getId());
        Long groupId = scheduleDTO.getGroupId();
        Double totalCount = (double) userRepository.countByGroupId(groupId);
        Double presentCount = (double) attendances.stream()
                .filter(att -> att.getScanType() == Attendance.ScanType.IN)
                .map(Attendance::getUser)
                .distinct()
                .count();
        Double statistic = totalCount > 0 ? (presentCount / totalCount) * 100 : 0.0;

        // Get all users in the group and create StudentDTO list
        List<User> users = userRepository.findByGroupId(groupId);
        List<StudentDTO> studentDTOs = users.stream().map(user -> {
            StudentDTO studentDTO = new StudentDTO();
            studentDTO.setId(user.getId());
            studentDTO.setName(user.getUserName());
            studentDTO.setEmail(user.getEmail());
            studentDTO.setPhoneNumber(user.getPhoneNumber());
            // Check if user has attendance record with ScanType.IN for this schedule
            boolean attended = attendances.stream()
                    .anyMatch(att -> att.getScanType() == Attendance.ScanType.IN
                            && att.getUser().getId().equals(user.getId()));
            studentDTO.setAttend(attended);
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


