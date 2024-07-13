package pb.se.TimeReportService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pb.se.TimeReportService.domain.User;
import pb.se.TimeReportService.domain.WorkLog;
import pb.se.TimeReportService.service.WorkLogService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/time-report/worklogs")
public class WorkLogController {

    @Autowired
    private WorkLogService workLogService;

    @GetMapping
    public List<WorkLog> getAllWorkLogs() {
        return workLogService.getAllWorkLogs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkLog> getWorkLogById(@PathVariable UUID id) {
        return workLogService.getWorkLogById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public WorkLog createWorkLog(@RequestBody WorkLog workLog) {
        return workLogService.createWorkLog(workLog);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkLog> updateWorkLog(@PathVariable UUID id, @RequestBody WorkLog workLog) {
        return workLogService.updateWorkLog(id, workLog)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkLog(@PathVariable UUID id) {
        if (workLogService.deleteWorkLog(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public List<WorkLog> searchWorkLogs(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {

        User user = new User(); //TODO: get user from security context
        return workLogService.findWorkLogsByUserAndDateRange(user, startDate, endDate);
    }
}
