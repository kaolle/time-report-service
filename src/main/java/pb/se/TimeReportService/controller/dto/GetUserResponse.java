package pb.se.TimeReportService.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import pb.se.TimeReportService.domain.User;

import java.util.List;

public class GetUserResponse {
    @JsonProperty
    private final List<CustomerResponse> customers;

    public GetUserResponse(User user) {
        this.customers = user.getCustomers().stream().map(CustomerResponse::new).toList();
    }
}
