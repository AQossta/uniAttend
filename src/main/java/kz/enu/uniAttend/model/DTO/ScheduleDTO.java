package kz.enu.uniAttend.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleDTO {
    private Long id;
    private String subject;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long groupId;
    private Long teacherId;
    private String teacherName;
    private String groupName;
}
