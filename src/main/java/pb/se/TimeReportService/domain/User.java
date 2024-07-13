package pb.se.TimeReportService.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collections;
import java.util.List;

@Document(collection = "users")
public class User {

    @Id
    private String username;

    private String password;

    @DBRef
    private List<Customer> customers;

    public User() {
    }

    public User(String username) {
        this.username = username;
        this.password = "N/A";
        this.customers = Collections.emptyList();
    }
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.customers = Collections.emptyList();
    }

    public String getUsername() {
        return username;
    }
    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    public List<Customer> getCustomers() {
        return customers;
    }
}
