package pb.se.TimeReportService.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import pb.se.TimeReportService.domain.Task;

import java.util.UUID;
@SuppressWarnings("unused")
public class GetTaskResponse {
    @JsonProperty
    private final UUID id;
    @JsonProperty
    private final UUID customerId;
    @JsonProperty
    private final String title;
    @JsonProperty
    private final String customerCode;

    public GetTaskResponse(Task task) {
        this.id = task.getId();
        this.customerId = task.getCustomer().getId();
        this.title = task.getTitle();
        this.customerCode = task.getCustomerCode();
    }
}
