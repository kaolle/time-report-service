package pb.se.TimeReportService.port.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import pb.se.TimeReportService.domain.Customer;

import java.util.UUID;

public interface CustomerRepository extends MongoRepository<Customer, UUID> {
}
