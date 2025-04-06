package kz.enu.uniAttend.model.DTO;

import lombok.Data;

@Data
public class StatisticsPanelDTO {
    private ScheduleDTO scheduleDTO;
    private Double totalCount;
    private Double presentCount;
    private Double statistic;
}
