package kz.enu.uniAttend.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentDTO {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private boolean attend;
    private LocalDateTime attendTime; // Время входа (ScanType.IN)
    private LocalDateTime exitTime;   // Время выхода (ScanType.OUT)
    private long attendanceDuration;
}
