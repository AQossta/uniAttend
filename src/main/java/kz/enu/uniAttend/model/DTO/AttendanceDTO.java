package kz.enu.uniAttend.model.DTO;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class AttendanceDTO {
    private Long userId;
    private Long scheduleId;
    private LocalDateTime scanInTime;
    private LocalDateTime scanOutTime;
    private Long minutesPresent;
}
