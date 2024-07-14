package pb.se.TimeReportService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pb.se.TimeReportService.controller.dto.TaskRequest;
import pb.se.TimeReportService.domain.Customer;
import pb.se.TimeReportService.domain.Task;
import pb.se.TimeReportService.domain.User;
import pb.se.TimeReportService.exception.ForbiddenException;
import pb.se.TimeReportService.exception.TaskNotFoundException;
import pb.se.TimeReportService.port.persistence.TaskRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private CustomerService customerService;

    public Task getTaskById(User user, UUID id) {
        Task task = taskRepository.findById(id).orElseThrow(TaskNotFoundException::new);
        validateUser(user, task);
        return task;
    }

    public Task createTask(User user, TaskRequest taskRequest) {
        Customer customer = customerService.getCustomerById(user, taskRequest.customerId());
        Task task = new Task(user, taskRequest.title(), taskRequest.customerCode(), customer);
        Task saveTask = taskRepository.save(task);
        customer.addTask(saveTask);
        customerService.updateCustomer(user, customer.getId(), customer);
        return saveTask;
    }

    public Optional<Task> updateTask(User user, UUID id, Task task) {
        return taskRepository.findById(id)
                .map(existingTask -> {
                    existingTask.setTitle(task.getTitle());
                    existingTask.setCustomerCode(task.getCustomerCode());
                    return taskRepository.save(existingTask);
                });
    }

    public boolean deleteTask(User user, UUID id) {
        validateUser(user, taskRepository.findById(id).orElseThrow(TaskNotFoundException::new));
        return taskRepository.findById(id)
                .map(task -> {
                    taskRepository.delete(task);
                    return true;
                })
                .orElse(false);
    }

    private static void validateUser(User user, Task task) {
        if (!task.getUser().equals(user)) {
            throw new ForbiddenException();
        }
    }

}
