package pb.se.TimeReportService.port.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import pb.se.TimeReportService.domain.Task;

import java.util.UUID;

public interface TaskRepository extends MongoRepository<Task, UUID> {
}
