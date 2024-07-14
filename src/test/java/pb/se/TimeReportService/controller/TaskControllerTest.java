package pb.se.TimeReportService.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import pb.se.TimeReportService.domain.User;
import pb.se.TimeReportService.domain.WhiteListedUser;
import pb.se.TimeReportService.port.persistence.CustomerRepository;
import pb.se.TimeReportService.port.persistence.TaskRepository;
import pb.se.TimeReportService.port.persistence.UserRepository;
import pb.se.TimeReportService.port.persistence.WhiteListedUserRepository;

import java.util.Optional;
import java.util.UUID;

import static com.mongodb.assertions.Assertions.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.test.util.AssertionErrors.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class TaskControllerTest {


    public static final String USERNAME = "baba";
    @Autowired
    UserRepository userRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    WhiteListedUserRepository whiteListedUserRepository;
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    TestRestTemplate restTemplate;
    private String accessToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        whiteListedUserRepository.deleteAll();
        customerRepository.deleteAll();
        taskRepository.deleteAll();

        whiteListedUserRepository.save(new WhiteListedUser(USERNAME));
        accessToken = signUp(USERNAME, "säkert");
    }

    @Test
    public void canAddTaskToCustomer() throws Exception {

        UUID customerId = addCustomerToUser("SHB");
        JsonObject json = new JsonObject();
        json.addProperty("customerId", customerId.toString());
        json.addProperty("title", "Fixa kaffe");
        json.addProperty("customerCode", "tuppen 4711");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/time-report/tasks", entity, String.class);

        JsonObject responseBody = new Gson().fromJson(response.getBody(), JsonObject.class);
        var taskId = UUID.fromString(responseBody.get("id").getAsString());
        taskRepository.findById(taskId).ifPresentOrElse(task -> {
                    assertThat(task.getUser().getUsername(), Matchers.is(USERNAME));
                    assertThat(task.getCustomer().getId(), Matchers.is(customerId));
                    assertThat(task.getTitle(), Matchers.is("Fixa kaffe"));
                    assertThat(task.getCustomerCode(), Matchers.is("tuppen 4711"));
                }, () -> fail("Task not found")
        );
        customerRepository.findById(customerId).ifPresentOrElse(customer -> {
                    assertThat(customer.getTasks(), hasSize(1));
                    assertThat(customer.getTasks().get(0).getId(), Matchers.is(taskId));
                }, () -> fail("Customer not found")
        );

    }

    private UUID addCustomerToUser(String customerName) {
        // Add a customer to user by rest call
        JsonObject json = new JsonObject();
        json.addProperty("name", customerName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/time-report/customers", entity, String.class);
        JsonObject responseBody = new Gson().fromJson(response.getBody(), JsonObject.class);
        return UUID.fromString(responseBody.get("id").getAsString());
    }

    private String signUp(String username, String password) {
        // signup
        JsonObject json = new JsonObject();
        json.addProperty("username", username);
        json.addProperty("password", password);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/auth/signup", entity, String.class);

        JsonObject responseBody = new Gson().fromJson(response.getBody(), JsonObject.class);
        assertThat(response.getStatusCode(), Matchers.is(CREATED));

        Optional<User> userRepositoryById = userRepository.findById(username);
        assertTrue(userRepositoryById.isPresent());

        return responseBody.get("accessToken").getAsString();
    }
}
