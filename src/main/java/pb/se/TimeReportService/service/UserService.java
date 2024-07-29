package pb.se.TimeReportService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pb.se.TimeReportService.annotation.UserNotFoundException;
import pb.se.TimeReportService.domain.Customer;
import pb.se.TimeReportService.domain.Task;
import pb.se.TimeReportService.domain.User;
import pb.se.TimeReportService.exception.TaskNotFoundException;
import pb.se.TimeReportService.port.persistence.UserRepository;

import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    public User getUser(String username) {
        return userRepository.findById(username).orElseThrow(UserNotFoundException::new);
    }

    public User getUser(User user) {
        return userRepository.findById(user.getUsername()).orElseThrow(UserNotFoundException::new);
    }

    public User update(User user) {
        return userRepository.save(user);
    }

    public boolean deleteUser(String username) {
        return userRepository.findById(username)
                .map(user -> {
                    userRepository.delete(user);
                    return true;
                })
                .orElse(false);
    }

    public User addCustomerToUser(User currentUser, Customer customer) {
        currentUser.getCustomers().add(customer);
        return userRepository.save(currentUser);
    }

    public User addTaskToCustomer(User currentUser, UUID customerId, Task task) {
        currentUser.getCustomers().stream()
                .filter(customer -> customer.getId().equals(customerId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Customer not found"))
                .getTasks().add(task);
        return userRepository.save(currentUser);
    }

    public void deleteTaskFromCustomer(User user, UUID taskId) {
        boolean taskFound = user.getCustomers().stream()
                .flatMap(customer -> customer.getTasks().stream())
                .anyMatch(task -> task.getId().equals(taskId));

        if (!taskFound) {
            throw new TaskNotFoundException();
        }
        user.getCustomers().forEach(customer -> customer.getTasks().removeIf(task -> task.getId().equals(taskId)));
        userRepository.save(user);
    }
}
