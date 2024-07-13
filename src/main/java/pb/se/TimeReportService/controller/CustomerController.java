package pb.se.TimeReportService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pb.se.TimeReportService.domain.Customer;
import pb.se.TimeReportService.domain.User;
import pb.se.TimeReportService.service.CustomerService;
import pb.se.TimeReportService.service.UserService;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(CustomerController.TIME_REPORT_CUSTOMERS)
public class CustomerController {

    public static final String BABA = "baba";
    public static final String TIME_REPORT_CUSTOMERS = "/time-report/customers";
    @Autowired
    private CustomerService customerService;
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable UUID id) {
        return customerService.getCustomerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping
    public ResponseEntity<Customer>  addCustomer(@RequestBody Customer customer) {
        Optional<User> user = userService.getUserByUsername(BABA); //TODO change to get current user
        user.ifPresent(
                u -> customerService.addCustomer(u, customer)
        );
        return ResponseEntity.created(URI.create(TIME_REPORT_CUSTOMERS+"/"+customer.getId())).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable UUID id, @RequestBody Customer customer) {
        return customerService.updateCustomer(id, customer)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID id) {
        if (customerService.deleteCustomer(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
