package pb.se.TimeReportService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pb.se.TimeReportService.annotation.CurrentUser;
import pb.se.TimeReportService.domain.Customer;
import pb.se.TimeReportService.domain.User;
import pb.se.TimeReportService.service.CustomerService;
import pb.se.TimeReportService.service.UserService;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping(CustomerController.TIME_REPORT_CUSTOMERS_PATH)
public class CustomerController {

    public static final String TIME_REPORT_CUSTOMERS_PATH = "/time-report/customers";
    @Autowired
    private CustomerService customerService;
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@CurrentUser User user, @PathVariable UUID id) {
        Customer customer = customerService.getCustomerById(user, id);
        return ResponseEntity.ok(customer);
    }

    @PostMapping
    public ResponseEntity<Customer> addCustomer(@CurrentUser User user, @RequestBody Customer customer) {
        Customer addedCustomer = customerService.addCustomer(user, customer);
        URI location = URI.create(String.format("%s/%s", TIME_REPORT_CUSTOMERS_PATH, addedCustomer.getId()));
        return ResponseEntity.created(location).body(addedCustomer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@CurrentUser User user, @PathVariable UUID id, @RequestBody Customer customer) {
        return customerService.updateCustomer(user, id, customer)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@CurrentUser User user, @PathVariable UUID id) {
        customerService.deleteCustomer(id, user);
        return ResponseEntity.noContent().build();
    }

}
