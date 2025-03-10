package kz.enu.uniAttend.model.request;

import lombok.Data;

@Data
public class ScanRequest {
    private Long qrCodeId;
    private Double latitude;
    private Double longitude;
    private String scanType;
}
