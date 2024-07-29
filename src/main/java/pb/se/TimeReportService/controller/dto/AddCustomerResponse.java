package pb.se.TimeReportService.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import pb.se.TimeReportService.domain.Customer;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class AddCustomerResponse {
    @JsonProperty
    private final UUID id;
    @JsonProperty
    private final String name;
    @JsonProperty
    private final List<AddTaskResponse> tasks;
    public AddCustomerResponse(Customer customer) {
        this.id = customer.getId();
        this.name = customer.getName();
        this.tasks = customer.getTasks().stream().map(AddTaskResponse::new).collect(Collectors.toList());
    }
}
