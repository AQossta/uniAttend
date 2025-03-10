package kz.enu.uniAttend.service;

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

    public Schedule createSchedule(ScheduleRequest request) throws Exception {
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

        return scheduleRepository.save(schedule);
    }

    public Schedule getScheduleById(Long scheduleId) throws Exception {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new Exception("Schedule not found"));
    }

    public List<Schedule> getSchedulesByGroup(Long groupId) {
        return scheduleRepository.findByGroupId(groupId);
    }

    public List<Schedule> getSchedulesByLecturer(Long lecturerId) {
        return scheduleRepository.findByLecturerId(lecturerId);
    }
}

