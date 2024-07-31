package pb.se.TimeReportService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pb.se.TimeReportService.annotation.CurrentUser;
import pb.se.TimeReportService.controller.dto.CreateWorkLogResponse;
import pb.se.TimeReportService.controller.dto.WorkLogRequest;
import pb.se.TimeReportService.controller.dto.WorkLogResponse;
import pb.se.TimeReportService.domain.User;
import pb.se.TimeReportService.domain.WorkLog;
import pb.se.TimeReportService.service.WorkLogService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/time-report/worklogs")
public class WorkLogController {

    @Autowired
    private WorkLogService workLogService;


    @GetMapping("/{id}")
    public ResponseEntity<WorkLog> getWorkLogById(@CurrentUser User user, @PathVariable UUID id) {
        return workLogService.getWorkLogById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CreateWorkLogResponse> createWorkLog(@CurrentUser User user, @RequestBody WorkLogRequest workLog) {
        WorkLog createdWorkLog = workLogService.createWorkLog(user, new WorkLog(user, workLog.getDate(), workLog.getTaskId(), workLog.getHoursWorked()));
        return new ResponseEntity<>(new CreateWorkLogResponse(createdWorkLog), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkLog> updateWorkLog(@CurrentUser User user, @PathVariable UUID id, @RequestBody WorkLog workLog) {
        workLogService.updateWorkLog(user, id, workLog);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkLog(@CurrentUser User user, @PathVariable UUID id) {
        workLogService.deleteWorkLog(user, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<WorkLogResponse> searchWorkLogs(@CurrentUser User user, @RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        return workLogService.findWorkLogsByUserAndDateRange(user, startDate, endDate).stream().map(WorkLogResponse::new).collect(Collectors.toList());
    }
}
