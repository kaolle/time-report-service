package pb.se.TimeReportService.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;
@SuppressWarnings("unused")
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

    public Task(User user, String title, String customerCode, Customer customer) {
        this.user = user;
        this.id = UUID.randomUUID();
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
    public User getUser() {
        return user;
    }
}
