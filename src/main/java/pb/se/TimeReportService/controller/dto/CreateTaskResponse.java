package pb.se.TimeReportService.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import pb.se.TimeReportService.domain.Task;

import java.util.UUID;
@SuppressWarnings("unused")
public class CreateTaskResponse {
    @JsonProperty
    private final UUID id;

    public CreateTaskResponse(Task task) {
        this.id = task.getId();
    }

}
