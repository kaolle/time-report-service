package pb.se.TimeReportService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pb.se.TimeReportService.domain.User;
import pb.se.TimeReportService.domain.WorkLog;
import pb.se.TimeReportService.port.persistence.WorkLogRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WorkLogService {

    @Autowired
    private WorkLogRepository workLogRepository;

    public List<WorkLog> getAllWorkLogs() {
        return workLogRepository.findAll();
    }

    public Optional<WorkLog> getWorkLogById(UUID id) {
        return workLogRepository.findById(id);
    }

    public WorkLog createWorkLog(WorkLog workLog) {
        return workLogRepository.save(workLog);
    }

    public Optional<WorkLog> updateWorkLog(UUID id, WorkLog workLog) {
        return workLogRepository.findById(id)
                .map(existingWorkLog -> {
                    existingWorkLog.setDate(workLog.getDate());
                    existingWorkLog.setTask(workLog.getTask());
                    existingWorkLog.setHoursWorked(workLog.getHoursWorked());
                    return workLogRepository.save(existingWorkLog);
                });
    }

    public boolean deleteWorkLog(UUID id) {
        return workLogRepository.findById(id)
                .map(workLog -> {
                    workLogRepository.delete(workLog);
                    return true;
                })
                .orElse(false);
    }

    public List<WorkLog> findWorkLogsByUserAndDateRange(User user, LocalDate startDate, LocalDate endDate) {
        return workLogRepository.findByUserAndDateBetween(user, startDate.minusDays(1), endDate.plusDays(1));
    }

}
