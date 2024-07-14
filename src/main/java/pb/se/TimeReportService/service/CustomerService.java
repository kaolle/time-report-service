package pb.se.TimeReportService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pb.se.TimeReportService.domain.Customer;
import pb.se.TimeReportService.domain.User;
import pb.se.TimeReportService.exception.CustomerNotFoundException;
import pb.se.TimeReportService.exception.ForbiddenException;
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

    public Customer getCustomerById(User user, UUID id) {
        validateUserIsTheOwner(user, id);
        return customerRepository.findById(id).orElseThrow(CustomerNotFoundException::new);
    }

    public Optional<Customer> updateCustomer(User user, UUID id, Customer customer) {
        validateUserIsTheOwner(user, id);
        return customerRepository.findById(id)
                .map(existingCustomer -> {
                    existingCustomer.setName(customer.getName());
                    existingCustomer.setTasks(customer.getTasks());
                    return customerRepository.save(existingCustomer);
                });
    }

    public void deleteCustomer(UUID id, User user) {
        validateUserIsTheOwner(user, id);
        Customer customer = customerRepository.findById(id).orElseThrow(CustomerNotFoundException::new);

        customerRepository.delete(customer);
    }

    public Customer addCustomer(User user, Customer customer) {
        Customer saved = customerRepository.save(customer);
        user.addCustomer(customer);
        userService.update(user);
        return saved;
    }
    private static void validateUserIsTheOwner(User user, UUID id) {
        if (user.getCustomers().stream().noneMatch(c-> c.getId().equals(id))) {
            throw new ForbiddenException();
        }
    }

}
