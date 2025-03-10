package kz.enu.uniAttend.controller;

import kz.enu.uniAttend.model.response.MessageResponse;
import kz.enu.uniAttend.service.QrCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/teacher/qr")
@RequiredArgsConstructor
public class QrCodeController {

    private final QrCodeService qrCodeService;

    @PostMapping("/generate")
    public MessageResponse<String> generateQrCode(@RequestParam Long scheduleId) throws Exception {
            return MessageResponse.of(qrCodeService.generateQrCode(scheduleId));
    }

    @GetMapping("/get")
    public MessageResponse<String> getQrCode(@RequestParam Long scheduleId) throws Exception {
            return MessageResponse.of(qrCodeService.getQrCode(scheduleId));
    }
}
