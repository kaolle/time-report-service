package pb.se.TimeReportService.controller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.UUID;

public class WorkLogRequest {


    private final LocalDate date;

    private final UUID taskId;

    private final int hoursWorked;

    @JsonCreator
    public WorkLogRequest(@JsonProperty LocalDate date, @JsonProperty UUID taskId, @JsonProperty int hoursWorked) {
        this.date = date;
        this.taskId = taskId;
        this.hoursWorked = hoursWorked;
    }

    public LocalDate getDate() {
        return date;
    }

    public UUID getTaskId() {
        return taskId;
    }

    public int getHoursWorked() {
        return hoursWorked;
    }
}

