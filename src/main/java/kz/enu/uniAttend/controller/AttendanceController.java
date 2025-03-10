package kz.enu.uniAttend.controller;

import kz.enu.uniAttend.model.DTO.AttendanceDTO;
import kz.enu.uniAttend.model.request.ScanRequest;
import kz.enu.uniAttend.model.response.MessageResponse;
import kz.enu.uniAttend.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/student/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/scan")
    public MessageResponse<?> scanQrCode(@RequestBody ScanRequest request, @PathVariable Long studentId) throws Exception {
            AttendanceDTO response = attendanceService.scanQrCode(request, studentId);
            return MessageResponse.of(ResponseEntity.ok(response));
    }

}