package kz.enu.uniAttend.controller;

import kz.enu.uniAttend.model.entity.Schedule;
import kz.enu.uniAttend.model.request.ScheduleRequest;
import kz.enu.uniAttend.model.response.MessageResponse;
import kz.enu.uniAttend.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping("teacher/schedule/create")
//    @PreAuthorize("hasRole('TEACHER')")
    public MessageResponse<?> createSchedule(@RequestBody ScheduleRequest request) throws Exception {
        Schedule schedule = scheduleService.createSchedule(request);
        return MessageResponse.of(schedule);
    }

    @GetMapping("student/schedule/{scheduleId}")
//    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public MessageResponse<?> getScheduleById(@PathVariable Long scheduleId) throws Exception {
        Schedule schedule = scheduleService.getScheduleById(scheduleId);
        return MessageResponse.of(schedule);
    }

    @GetMapping("student/schedule/group/{groupId}")
//    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public MessageResponse<?> getSchedulesByGroup(@PathVariable Long groupId) {
        List<Schedule> schedules = scheduleService.getSchedulesByGroup(groupId);
        return MessageResponse.of(schedules);
    }

    @GetMapping("teacher/schedule/lecturer/{lecturerId}")
//    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> getSchedulesByLecturer(@PathVariable Long lecturerId) {
        List<Schedule> schedules = scheduleService.getSchedulesByLecturer(lecturerId);
        return ResponseEntity.ok(schedules);
    }
}
