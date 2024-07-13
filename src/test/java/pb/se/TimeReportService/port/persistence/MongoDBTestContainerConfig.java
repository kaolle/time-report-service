package pb.se.TimeReportService.port.persistence;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;

@Configuration
@EnableMongoRepositories
public class MongoDBTestContainerConfig {
    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    static {
        mongoDBContainer.start();
        var mappedPort = mongoDBContainer.getFirstMappedPort();
        System.setProperty("mongodb.container.port", String.valueOf(mappedPort));
        System.setProperty("SPRING_DATA_MONGODB_URI", "mongodb://localhost:" + mappedPort + "/test");
    }
}
