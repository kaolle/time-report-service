package pb.se.TimeReportService.port.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import pb.se.TimeReportService.domain.WorkLog;
import pb.se.TimeReportService.domain.User;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface WorkLogRepository extends MongoRepository<WorkLog, UUID> {
    List<WorkLog> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);
}
