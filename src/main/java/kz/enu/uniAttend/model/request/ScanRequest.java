package kz.enu.uniAttend.model.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ScanRequest {
    private Long userId;
    private String code; // вместо scheduleId
    private String scanType;
    private Double latitude;
    private Double longitude;
}