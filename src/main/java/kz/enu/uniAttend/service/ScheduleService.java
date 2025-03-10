package kz.enu.uniAttend.service;

import kz.enu.uniAttend.model.DTO.ScheduleDTO;
import kz.enu.uniAttend.model.entity.Group;
import kz.enu.uniAttend.model.entity.Schedule;
import kz.enu.uniAttend.model.entity.Subject;
import kz.enu.uniAttend.model.entity.User;
import kz.enu.uniAttend.model.request.ScheduleRequest;
import kz.enu.uniAttend.repository.GroupRepository;
import kz.enu.uniAttend.repository.ScheduleRepository;
import kz.enu.uniAttend.repository.SubjectRepository;
import kz.enu.uniAttend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final GroupRepository groupRepository;

    public String createSchedule(ScheduleRequest request) throws Exception {
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new Exception("Subject not found"));
        User lecturer = userRepository.findById(request.getLecturerId())
                .orElseThrow(() -> new Exception("Lecturer not found"));
        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new Exception("Group not found"));

        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new Exception("End time must be after start time");
        }

        Schedule schedule = new Schedule();
        schedule.setSubject(subject);
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        schedule.setGroup(group);
        schedule.setLecturer(lecturer);

        scheduleRepository.save(schedule);
        return "Предмет успешно добавлен в расписание";
    }

    public ScheduleDTO getScheduleById(Long scheduleId) throws Exception {
        return convertToScheduleDTO(scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new Exception("Schedule not found")));
    }

    public List<ScheduleDTO> getSchedulesByGroup(Long groupId) {
        return scheduleRepository.findByGroupId(groupId).stream().map(this::convertToScheduleDTO).toList();
    }

    public List<ScheduleDTO> getSchedulesByLecturer(Long lecturerId) {
        return scheduleRepository.findByLecturerId(lecturerId).stream().map(this::convertToScheduleDTO).toList();
    }

    private ScheduleDTO convertToScheduleDTO(Schedule schedule) {
        return new ScheduleDTO(schedule.getId(), schedule.getSubject().getName(), schedule.getStartTime(), schedule.getEndTime(), schedule.getGroup().getId(), schedule.getLecturer().getId(), schedule.getLecturer().getUserName(), schedule.getGroup().getName());
    }
//
//    private Long id;
//    private String subject;
//    private LocalDateTime startTime;
//    private LocalDateTime endTime;
//    private Long groupId;
//    private Long teacherId;
//    private String teacherName;
//    private String groupName;
}

