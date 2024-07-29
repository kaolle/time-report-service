package pb.se.TimeReportService.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;
@SuppressWarnings("unused")
public class Task {

    private UUID id;

    private String title;

    private String customerCode;

    // Constructors, Getters, and Setters
    public Task() {

    }
    @JsonCreator
    public Task(@JsonProperty String title, @JsonProperty String customerCode) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.customerCode = customerCode;
    }
    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }
}
