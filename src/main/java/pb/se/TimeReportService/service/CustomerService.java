package pb.se.TimeReportService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pb.se.TimeReportService.domain.Customer;
import pb.se.TimeReportService.domain.User;
import pb.se.TimeReportService.port.persistence.CustomerRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private UserService userService;

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Optional<Customer> getCustomerById(UUID id) {
        return customerRepository.findById(id);
    }

    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public Optional<Customer> updateCustomer(UUID id, Customer customer) {
        return customerRepository.findById(id)
                .map(existingCustomer -> {
                    existingCustomer.setName(customer.getName());
                    return customerRepository.save(existingCustomer);
                });
    }

    public boolean deleteCustomer(UUID id) {
        return customerRepository.findById(id)
                .map(customer -> {
                    customerRepository.delete(customer);
                    return true;
                })
                .orElse(false);
    }

    public void addCustomer(User user, Customer customer) {
        customerRepository.save(customer);
        user.addCustomer(customer);
        userService.update(user);
    }
}
