package pb.se.TimeReportService.port.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import pb.se.TimeReportService.domain.User;

public interface UserRepository extends MongoRepository<User, String> {
    // Custom query methods can be defined here if needed
}
