package kz.enu.uniAttend.controller;

import kz.enu.uniAttend.model.DTO.ScheduleDTO;
import kz.enu.uniAttend.model.DTO.StatisticsPanelDTO;
import kz.enu.uniAttend.model.entity.Schedule;
import kz.enu.uniAttend.model.request.ScheduleRequest;
import kz.enu.uniAttend.model.response.MessageResponse;
import kz.enu.uniAttend.service.ScheduleService;
import kz.enu.uniAttend.service.StatisticsPanelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final StatisticsPanelService statisticsPanelService;

    @PostMapping("teacher/schedule/create")
//    @PreAuthorize("hasRole('TEACHER')")
    public MessageResponse<String> createSchedule(@RequestBody ScheduleRequest request) throws Exception {
        return MessageResponse.of(scheduleService.createSchedule(request));
    }

    @GetMapping("student/schedule/{scheduleId}")
//    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public MessageResponse<?> getScheduleById(@PathVariable Long scheduleId) throws Exception {
        return MessageResponse.of(scheduleService.getScheduleById(scheduleId));
    }

    @GetMapping("student/schedule/group/{groupId}")
//    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public MessageResponse<List<ScheduleDTO>> getSchedulesByGroup(@PathVariable Long groupId) {
        return MessageResponse.of(scheduleService.getSchedulesByGroup(groupId));
    }

    @GetMapping("teacher/schedule/lecturer/{lecturerId}")
//    @PreAuthorize("hasRole('TEACHER')")
    public MessageResponse<List<ScheduleDTO>> getSchedulesByLecturer(@PathVariable Long lecturerId) {
        return MessageResponse.of(scheduleService.getSchedulesByLecturer(lecturerId));
    }

    @GetMapping("teacher/schedule/{scheduleId}")
    public MessageResponse<StatisticsPanelDTO> getStatisticById(@PathVariable Long scheduleId) {
        return MessageResponse.of(statisticsPanelService.getAttendanceStatistics(scheduleId));
    }
}
