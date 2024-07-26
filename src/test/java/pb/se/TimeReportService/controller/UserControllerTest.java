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
import org.springframework.http.HttpMethod;
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
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.util.AssertionErrors.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserControllerTest {


    public static final String USERNAME = "baba";
    @Autowired
    UserRepository userRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    WhiteListedUserRepository whiteListedUserRepository;
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
    public void canSignUpAndAddCustomerToUser() throws Exception {

        // Add a customer to user by rest call
        JsonObject json = new JsonObject();
        json.addProperty("name", "SHB");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity( "/time-report/customers", entity, String.class);

        assertThat(response.getStatusCode(), Matchers.is(CREATED));

        // Check that the customer is added to the user
        User userWithCustomers = userRepository.findById(USERNAME).get();
        assertThat(userWithCustomers.getCustomers(), hasSize(1));

    }

    @Test
    public void canAddTaskToCustomer() throws Exception {

        UUID customerId = addCustomerToUser("SHB");
        ResponseEntity<String> response = addTaskToCustomer(customerId, "Fixa kaffe", "tuppen 4711");

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


    @Test
    public void canGetAllCustomersAndTasks() throws Exception {

        UUID customerId1 = addCustomerToUser("Customer1");
        addTaskToCustomer(customerId1, "Task1", "Code1");
        addTaskToCustomer(customerId1, "Task2", "Code2");
        UUID customerId2 = addCustomerToUser("Customer2");
        addTaskToCustomer(customerId2, "Task3", "Code3");
        addTaskToCustomer(customerId2, "Task4", "Code4");

       //get
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange("/time-report/users", HttpMethod.GET, entity, String.class);
        assertThat(response.getStatusCode(), Matchers.is(OK));
        JsonObject responseBody = new Gson().fromJson(response.getBody(), JsonObject.class);
        assertThat(responseBody.get("customers").getAsJsonArray().size(), Matchers.is(2));
        assertThat(responseBody.get("customers").getAsJsonArray().get(0).getAsJsonObject().get("tasks").getAsJsonArray().size(), Matchers.is(2));
        assertThat(responseBody.get("customers").getAsJsonArray().get(1).getAsJsonObject().get("tasks").getAsJsonArray().size(), Matchers.is(2));
    }

    @Test
    public void canDeleteTaskFromCustomer() throws Exception {

        UUID customerId = addCustomerToUser("Customer1");
        ResponseEntity<String> response = addTaskToCustomer(customerId, "Task1", "Code1");
        JsonObject responseBody = new Gson().fromJson(response.getBody(), JsonObject.class);
        var taskId = UUID.fromString(responseBody.get("id").getAsString());

        //delete task from customer's task list
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        response = restTemplate.exchange("/time-report/tasks/" + taskId, HttpMethod.DELETE, entity, String.class);
        assertThat(response.getStatusCode(), Matchers.is(NO_CONTENT));

        //check if task is deleted
        assertTrue(taskRepository.findById(taskId).isEmpty());
        //check if task is deleted from customer's task list
        customerRepository.findById(customerId).ifPresentOrElse(customer -> {
                    assertThat(customer.getTasks(), hasSize(0));
                }, () -> fail("Customer not found")
        );
    }

    private String signUp(String username, String password) {
        // signup
        JsonObject json = new JsonObject();
        json.addProperty("username", username);
        json.addProperty("password", password);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity( "/auth/signup", entity, String.class);

        JsonObject responseBody= new Gson().fromJson(response.getBody(), JsonObject.class);
        assertThat(response.getStatusCode(), Matchers.is(CREATED));

        Optional<User> userRepositoryById = userRepository.findById(username);
        assertTrue(userRepositoryById.isPresent());

        return responseBody.get("accessToken").getAsString();
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

    private ResponseEntity<String> addTaskToCustomer(UUID customerId, String title, String customerCode) {
        JsonObject json = new JsonObject();
        json.addProperty("customerId", customerId.toString());
        json.addProperty("title", title);
        json.addProperty("customerCode", customerCode);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/time-report/tasks", entity, String.class);
        return response;
    }

}
