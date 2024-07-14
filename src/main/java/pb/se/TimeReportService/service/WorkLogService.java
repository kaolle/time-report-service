package pb.se.TimeReportService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pb.se.TimeReportService.domain.User;
import pb.se.TimeReportService.domain.WorkLog;
import pb.se.TimeReportService.exception.ForbiddenException;
import pb.se.TimeReportService.exception.WorkLogNotFoundException;
import pb.se.TimeReportService.port.persistence.WorkLogRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WorkLogService {

    @Autowired
    private WorkLogRepository workLogRepository;

    public Optional<WorkLog> getWorkLogById(UUID id) {
        return workLogRepository.findById(id);
    }

    public WorkLog createWorkLog(User user, WorkLog workLog) {
        return workLogRepository.save(workLog.assignUser(user));
    }

    public void updateWorkLog(User user, UUID id, WorkLog updatedWorkLog) {
        WorkLog existingWorkLog = workLogRepository.findById(id).orElseThrow(WorkLogNotFoundException::new);
        validateUser(user, existingWorkLog);
        existingWorkLog.setDate(updatedWorkLog.getDate());
        existingWorkLog.setTask(updatedWorkLog.getTask());
        existingWorkLog.setHoursWorked(updatedWorkLog.getHoursWorked());
        workLogRepository.save(existingWorkLog);
    }

    public void deleteWorkLog(User user, UUID id) {
        WorkLog existingWorkLog = workLogRepository.findById(id).orElseThrow(WorkLogNotFoundException::new);
        validateUser(user, existingWorkLog);
        workLogRepository.delete(existingWorkLog);
    }

    public List<WorkLog> findWorkLogsByUserAndDateRange(User user, LocalDate startDate, LocalDate endDate) {
        return workLogRepository.findByUserAndDateBetween(user, startDate.minusDays(1), endDate.plusDays(1));
    }

    private static void validateUser(User user, WorkLog existingWorkLog) {
        if (!existingWorkLog.getUser().equals(user)) {
            throw new ForbiddenException();
        }
    }

}
