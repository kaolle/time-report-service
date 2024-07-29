package pb.se.TimeReportService.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import pb.se.TimeReportService.domain.User;

import java.util.List;

public class UserResponse {
    @JsonProperty
    private final List<AddCustomerResponse> customers;

    public UserResponse(User user) {
        this.customers = user.getCustomers().stream().map(AddCustomerResponse::new).toList();
    }
}
