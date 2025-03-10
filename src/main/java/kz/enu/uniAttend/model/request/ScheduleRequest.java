package kz.enu.uniAttend.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleRequest {
    private Long subjectId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long groupId;
    private Long lecturerId;
}

