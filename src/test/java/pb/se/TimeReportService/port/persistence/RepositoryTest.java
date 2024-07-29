package pb.se.TimeReportService.port.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;
import pb.se.TimeReportService.domain.Customer;
import pb.se.TimeReportService.domain.Task;
import pb.se.TimeReportService.domain.User;
import pb.se.TimeReportService.domain.WorkLog;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
@Testcontainers
@ContextConfiguration(classes = {MongoDBTestContainerConfig.class, MongoDBConfig.class})
class RepositoryTest {

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WorkLogRepository workLogRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        workLogRepository.deleteAll();
    }

    @Test
    void aUserWithItsCustomersCanBeSavedAndFetched() {
        // Create and save a user
        User user = new User("user1");
        userRepository.save(user);

        // Create and save a customer
        Customer customer = new Customer("Customer1");

        // Fetch the user and verify the associated customer
        User fetchedUser = userRepository.findById(user.getUsername()).get();
        fetchedUser.addCustomer(customer);
        userRepository.save(fetchedUser);

        assertEquals(user.getUsername(), fetchedUser.getUsername());
        assertEquals(customer.getName(), fetchedUser.getCustomers().get(0).getName());
    }

    @Test
    void aTaskCanBeAddedToCustomer() {
        User user = new User("user1");
        String name = "Test";
        userRepository.save(user);

        // Create and save a customer
        Customer customer = new Customer("Customer1");

        // Fetch the user and verify the associated customer
        User fetchedUser = userRepository.findById(user.getUsername()).get();
        fetchedUser.addCustomer(customer);
        userRepository.save(fetchedUser);

        // Create and save a task
        String taskTitle = "Task";
        Task task = new Task( taskTitle, "CustomerCode");
        fetchedUser = userRepository.findById(user.getUsername()).get();
        assertEquals(1, fetchedUser.getCustomers().size());
        fetchedUser.getCustomers().get(0).addTask(task);
        userRepository.save(fetchedUser);

        //verify that the task exist in the user and the customer
        User finalUserToVerify = userRepository.findById(user.getUsername()).get();
        assertEquals(1, finalUserToVerify.getCustomers().get(0).getTasks().size());
        assertEquals(taskTitle, finalUserToVerify.getCustomers().get(0).getTasks().get(0).getTitle());
    }

    @Test
    void aTaskCanBeAddedToWorkLog() {
        String name = "Test";
        Customer customer = new Customer(name);
        String taskTitle = "Task";
        Task task = new Task(taskTitle, "CustomerCode");

        WorkLog workLog = new WorkLog(UUID.randomUUID(), new User("baba"), LocalDate.now(), task.getId(), 8);
        workLogRepository.save(workLog);

        WorkLog workLogFromDb = workLogRepository.findById(workLog.getId()).get();

        assertEquals(task.getId(), workLogFromDb.getTaskId());
    }

    @Test
    void workLogsCanBeSearchedByUserAndDateRange() {
        User user = new User("baba");
        userRepository.save(user);
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();

        WorkLog workLog1 = new WorkLog(UUID.randomUUID(), user, startDate, UUID.randomUUID(), 8);
        WorkLog workLog2 = new WorkLog(UUID.randomUUID(), user, endDate, UUID.randomUUID(), 8);
        workLogRepository.save(workLog1);
        workLogRepository.save(workLog2);

        List<WorkLog> workLogs = workLogRepository.findByUserAndDateBetween(user, startDate.minusDays(1), endDate.plusDays(1));

        assertEquals(2, workLogs.size());
    }

    @Test
    void workLogsCanNotBeFetchByOtherUser() {
        User user1 = new User("user1");
        User user2 = new User("user2");
        userRepository.saveAll(List.of(user1, user2));
        LocalDate startDate = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        WorkLog workLog1 = new WorkLog(UUID.randomUUID(), user1, startDate.plusDays(5), UUID.randomUUID(), 8);
        WorkLog workLog2 = new WorkLog(UUID.randomUUID(), user1, startDate.plusDays(10), UUID.randomUUID(), 8);
        workLogRepository.save(workLog1);
        workLogRepository.save(workLog2);

        List<WorkLog> workLogs = workLogRepository.findByUserAndDateBetween(user2, startDate, endDate);

        assertEquals(0, workLogs.size());
    }
}
