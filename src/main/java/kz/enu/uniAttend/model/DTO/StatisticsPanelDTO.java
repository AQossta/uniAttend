package kz.enu.uniAttend.model.DTO;

import lombok.Data;

import java.util.List;

@Data
public class StatisticsPanelDTO {
    private ScheduleDTO scheduleDTO;
    private Double totalCount;
    private Double presentCount;
    private Double statistic;
    private List<StudentDTO> studentDTO;
}
