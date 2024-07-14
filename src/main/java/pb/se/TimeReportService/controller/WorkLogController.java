package pb.se.TimeReportService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pb.se.TimeReportService.annotation.CurrentUser;
import pb.se.TimeReportService.controller.dto.CreateWorkLogResponse;
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


    @GetMapping("/{id}")
    public ResponseEntity<WorkLog> getWorkLogById(@CurrentUser User user, @PathVariable UUID id) {
        return workLogService.getWorkLogById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public CreateWorkLogResponse createWorkLog(@CurrentUser User user, @RequestBody WorkLog workLog) {
        return new CreateWorkLogResponse(workLogService.createWorkLog(user, workLog));
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
    public List<WorkLog> searchWorkLogs(@CurrentUser User user, @RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        return workLogService.findWorkLogsByUserAndDateRange(user, startDate, endDate);
    }
}
