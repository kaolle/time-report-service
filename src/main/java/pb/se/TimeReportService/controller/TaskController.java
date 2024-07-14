package pb.se.TimeReportService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pb.se.TimeReportService.annotation.CurrentUser;
import pb.se.TimeReportService.controller.dto.GetTaskResponse;
import pb.se.TimeReportService.controller.dto.TaskRequest;
import pb.se.TimeReportService.controller.dto.CreateTaskResponse;
import pb.se.TimeReportService.domain.Task;
import pb.se.TimeReportService.domain.User;
import pb.se.TimeReportService.service.TaskService;

import java.util.UUID;

@RestController
@RequestMapping("/time-report/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;


    @GetMapping("/{id}")
    public ResponseEntity<GetTaskResponse> getTaskById(@CurrentUser User user, @PathVariable UUID id) {
        Task task = taskService.getTaskById(user, id);
        return ResponseEntity.ok(new GetTaskResponse(task));
    }

    @PostMapping
    public CreateTaskResponse createTask(@CurrentUser User user, @RequestBody TaskRequest taskRequest) {
        return new CreateTaskResponse(taskService.createTask(user, taskRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@CurrentUser User user, @PathVariable UUID id, @RequestBody Task task) {
        taskService.updateTask(user, id, task);
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@CurrentUser User user, @PathVariable UUID id) {
        taskService.deleteTask(user, id);
        return ResponseEntity.noContent().build();
    }
}
