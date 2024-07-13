package pb.se.TimeReportService.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "tasks")
public class Task {

    @Id
    private UUID id;
    @DBRef
    private User user;

    private String title;

    private String customerCode;
    @DBRef
    private Customer customer;

    // Constructors, Getters, and Setters
    public Task() {

    }

    public Task(UUID id, String title, String customerCode, Customer customer) {
        this.id = id;
        this.title = title;
        this.customerCode = customerCode;
        this.customer = customer;
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

    public Customer getCustomer() {
        return customer;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }
}
