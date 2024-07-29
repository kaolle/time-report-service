package pb.se.TimeReportService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pb.se.TimeReportService.annotation.CurrentUser;
import pb.se.TimeReportService.controller.dto.UserResponse;
import pb.se.TimeReportService.domain.Customer;
import pb.se.TimeReportService.domain.Task;
import pb.se.TimeReportService.domain.User;
import pb.se.TimeReportService.service.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/time-report/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping()
    public ResponseEntity<UserResponse> getUserAndAllCustomers(@CurrentUser User user) {
        return ResponseEntity.ok(new UserResponse(userService.getUser(user)));
    }

    @PostMapping("/customers")
    public ResponseEntity<UserResponse> addCustomerToUser(@CurrentUser User user, @RequestBody Customer customer) {
        User savedUser = userService.addCustomerToUser(user, customer);
        return new ResponseEntity<>(new UserResponse(savedUser), HttpStatus.CREATED);
    }

    @PostMapping("/customers/{customerId}/tasks")
    public ResponseEntity<UserResponse> addTaskToCustomer(@CurrentUser User user, @PathVariable UUID customerId, @RequestBody Task task) {
        User savedUser = userService.addTaskToCustomer(user, customerId, task);
        return new ResponseEntity<>(new UserResponse(savedUser), HttpStatus.CREATED);
    }

    @DeleteMapping("/customers/tasks/{taskId}")
    public ResponseEntity<Void> deleteTaskFromCustomer(@CurrentUser User user, @PathVariable UUID taskId) {
        userService.deleteTaskFromCustomer(user, taskId);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        //TODO validate that user can only sign-off themselves
        if (userService.deleteUser(username)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
