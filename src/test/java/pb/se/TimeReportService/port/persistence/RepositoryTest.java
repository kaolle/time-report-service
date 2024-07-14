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
    private CustomerRepository customerRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private WorkLogRepository workLogRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        customerRepository.deleteAll();
        taskRepository.deleteAll();
        workLogRepository.deleteAll();
    }

    @Test
    void aCustomerCanBeStoredAndFetched() {
        String name = "Test";
        Customer customer1 = new Customer(name);
        customerRepository.save(customer1);

        Customer customer = customerRepository.findById(customer1.getId()).get();

        assertEquals(name, customer.getName());
    }

    @Test
    void aUserWithItsCustomersCanBeSavedAndFetched() {
        // Create and save a user
        User user = new User("user1");
        userRepository.save(user);

        // Create and save a customer
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer("Customer1");
        customerRepository.save(customer);

        // Fetch the user and verify the associated customer
        User fetchedUser = userRepository.findById(user.getUsername()).get();
        fetchedUser.addCustomer(customer);
        userRepository.save(fetchedUser);

        assertEquals(user.getUsername(), fetchedUser.getUsername());
        assertEquals(customer.getName(), fetchedUser.getCustomers().get(0).getName());
    }

    @Test
    void aTaskCanBeAddedToCustomer() {
        String name = "Test";
        Customer customer = new Customer(name);
        customerRepository.save(customer);

        Customer customerFromDb = customerRepository.findById(customer.getId()).get();
        assertEquals(0, customerFromDb.getTasks().size());
        String taskTitle = "Task";
        Task task = new Task(new User("baba"), taskTitle, "CustomerCode", customer);
        taskRepository.save(task);
        customerFromDb.addTask(task);
        customerRepository.save(customerFromDb);

        Customer customerFromDb2 = customerRepository.findById(customer.getId()).get();
        assertEquals(1, customerFromDb2.getTasks().size());
        assertEquals(taskTitle, customerFromDb2.getTasks().get(0).getTitle());
    }

    @Test
    void aTaskCanBeAddedToWorkLog() {
        String name = "Test";
        Customer customer = new Customer(name);
        customerRepository.save(customer);
        String taskTitle = "Task";
        Task task = new Task(new User("baba"), taskTitle, "CustomerCode", customer);
        taskRepository.save(task);

        WorkLog workLog = new WorkLog(UUID.randomUUID(), new User("baba"), LocalDate.now(), task, 8);
        workLogRepository.save(workLog);

        WorkLog workLogFromDb = workLogRepository.findById(workLog.getId()).get();

        assertEquals(taskTitle, workLogFromDb.getTask().getTitle());
    }

    @Test
    void workLogsCanBeSearchedByUserAndDateRange() {
        User user = new User("baba");
        userRepository.save(user);
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();

        WorkLog workLog1 = new WorkLog(UUID.randomUUID(), user, startDate, new Task(user, "Task1", "CustomerCode1", new Customer()), 8);
        WorkLog workLog2 = new WorkLog(UUID.randomUUID(), user, endDate, new Task(user, "Task2", "CustomerCode2", new Customer()), 8);
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

        WorkLog workLog1 = new WorkLog(UUID.randomUUID(), user1, startDate.plusDays(5), new Task(user1, "Task1", "CustomerCode1", new Customer()), 8);
        WorkLog workLog2 = new WorkLog(UUID.randomUUID(), user1, startDate.plusDays(10), new Task(user1, "Task2", "CustomerCode2", new Customer()), 8);
        workLogRepository.save(workLog1);
        workLogRepository.save(workLog2);

        List<WorkLog> workLogs = workLogRepository.findByUserAndDateBetween(user2, startDate, endDate);

        assertEquals(0, workLogs.size());
    }
}
