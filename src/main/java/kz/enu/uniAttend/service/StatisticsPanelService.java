package kz.enu.uniAttend.service;

import kz.enu.uniAttend.model.DTO.AttendanceDTO;
import kz.enu.uniAttend.model.DTO.ScheduleDTO;
import kz.enu.uniAttend.model.DTO.StatisticsPanelDTO;
import kz.enu.uniAttend.model.entity.Attendance;
import kz.enu.uniAttend.repository.AttendanceRepository;
import kz.enu.uniAttend.repository.ScheduleRepository;
import kz.enu.uniAttend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
        StatisticsPanelDTO stats = new StatisticsPanelDTO();
        stats.setScheduleDTO(scheduleDTO);
        stats.setTotalCount(totalCount);
        stats.setPresentCount(presentCount);
        stats.setStatistic(statistic);
        return stats;
    }
}


