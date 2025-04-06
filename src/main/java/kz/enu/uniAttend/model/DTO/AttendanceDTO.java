package kz.enu.uniAttend.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceDTO {
    private Long id;
    private Long userId;
    private Long scheduleId;
    private LocalDateTime scanInTime;
    private LocalDateTime scanOutTime;
    private Long minutesPresent;
}
