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
    public MessageResponse<?> generateQrCode(@RequestParam Long scheduleId) throws Exception {
            String qrCodeBase64 = qrCodeService.generateQrCode(scheduleId);
            return MessageResponse.of(qrCodeBase64);
    }

    @GetMapping("/get")
    public ResponseEntity<?> getQrCode(@RequestParam Long scheduleId) throws Exception {
            String qrCodeBase64 = qrCodeService.getQrCode(scheduleId);
            return ResponseEntity.ok(qrCodeBase64);
    }
}
