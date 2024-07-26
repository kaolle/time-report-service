package pb.se.TimeReportService.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import pb.se.TimeReportService.domain.Customer;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CustomerResponse {
    @JsonProperty
    private final UUID id;
    @JsonProperty
    private final String name;
    @JsonProperty
    private final List<TaskResponse> tasks;
    public CustomerResponse(Customer customer) {
        this.id = customer.getId();
        this.name = customer.getName();
        this.tasks = customer.getTasks().stream().map(TaskResponse::new).collect(Collectors.toList());
    }
}
