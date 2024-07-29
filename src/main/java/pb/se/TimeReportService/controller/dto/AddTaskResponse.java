package pb.se.TimeReportService.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import pb.se.TimeReportService.domain.Task;

import java.util.UUID;

public class AddTaskResponse {
    @JsonProperty
    private final UUID id;
    @JsonProperty
    private final String title;
    @JsonProperty
    private final String customerCode;

    public AddTaskResponse(Task task) {
        this.id = task.getId();
        this.title = task.getTitle();
        this.customerCode = task.getCustomerCode();
    }
}
