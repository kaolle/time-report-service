package pb.se.TimeReportService.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
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

    public void removeCustomer(Customer customer) {
        customers.remove(customer);
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return new EqualsBuilder().append(username, user.username).append(password, user.password).append(customers, user.customers).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(username).append(password).append(customers).toHashCode();
    }
}
