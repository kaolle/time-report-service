package pb.se.TimeReportService.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import pb.se.TimeReportService.domain.WorkLog;

import java.time.LocalDate;
import java.util.UUID;

public class WorkLogResponse {
    @JsonProperty
    private final UUID id;
    @JsonProperty
    private final LocalDate date;
    @JsonProperty
    private final UUID taskId;
    @JsonProperty
    private final int hoursWorked;


    public WorkLogResponse(WorkLog workLog) {
        this.id = workLog.getId();
        this.date = workLog.getDate();
        this.taskId = workLog.getTaskId();
        this.hoursWorked = workLog.getHoursWorked();
    }

}
