package kz.enu.uniAttend.model.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ScanRequest {
    @NotNull(message = "Schedule ID cannot be null")
    @Positive(message = "Schedule ID must be positive")
    private Long scheduleId; // Изменили qrCodeId на scheduleId

    @NotNull(message = "Latitude cannot be null")
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private Double latitude;

    @NotNull(message = "Longitude cannot be null")
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private Double longitude;

    @NotNull(message = "Scan type cannot be null")
    @Pattern(regexp = "IN|OUT", message = "Scan type must be either 'IN' or 'OUT'")
    private String scanType;

    private Long userId;
}