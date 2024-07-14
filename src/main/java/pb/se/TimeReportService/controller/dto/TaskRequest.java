package pb.se.TimeReportService.controller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record TaskRequest(UUID customerId, String title, String customerCode) {
    @JsonCreator
    public TaskRequest(@JsonProperty(required = true) UUID customerId, @JsonProperty(required = true) String title, @JsonProperty String customerCode) {
        this.customerId = customerId;
        this.title = title;
        this.customerCode = customerCode;
    }
}
