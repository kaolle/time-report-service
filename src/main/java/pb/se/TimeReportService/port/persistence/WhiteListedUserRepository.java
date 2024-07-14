package pb.se.TimeReportService.port.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import pb.se.TimeReportService.domain.WhiteListedUser;

public interface WhiteListedUserRepository extends MongoRepository<WhiteListedUser, String> {
}
