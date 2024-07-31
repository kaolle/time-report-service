package pb.se.TimeReportService.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
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
import pb.se.TimeReportService.port.persistence.UserRepository;
import pb.se.TimeReportService.port.persistence.WhiteListedUserRepository;
import pb.se.TimeReportService.port.persistence.WorkLogRepository;

import java.util.Optional;
import java.util.UUID;

import static com.mongodb.assertions.Assertions.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class WorkLogControllerTest {


    public static final String USERNAME = "baba";
    public static final String WORKLOG_DATE = "2021-01-01";
    @Autowired
    UserRepository userRepository;
    @Autowired
    WhiteListedUserRepository whiteListedUserRepository;
    @Autowired
    WorkLogRepository workLogRepository;
    @Autowired
    TestRestTemplate restTemplate;
    private String accessToken;
    private UUID taskId;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        whiteListedUserRepository.deleteAll();

        whiteListedUserRepository.save(new WhiteListedUser(USERNAME));

        accessToken = signUp(USERNAME, "säkert");
        //add a customer
        UUID customerId = addCustomerToUser("Customer1");
        //add a task to the customer
        taskId = addTaskToCustomer(customerId, "Task1", "Code1");

    }

    @Test
    public void aUserCanAddAWorkLog() throws Exception {
        //create a work log
        ResponseEntity<String> response = createAWorkLogItem(taskId);

        assertThat(response.getStatusCode(), Matchers.is(CREATED));
        //verify get id from response body
        JsonObject responseBody = new Gson().fromJson(response.getBody(), JsonObject.class);
        UUID workLogId = UUID.fromString(responseBody.get("id").getAsString());
        //verify that work log is added
        assertTrue(workLogRepository.findById(workLogId).isPresent());
        //verify that work log is added
        assertThat(workLogRepository.findById(workLogId).get().getHoursWorked(), Matchers.is(8));
        assertThat(workLogRepository.findById(workLogId).get().getTaskId(), Matchers.is(taskId));
        assertThat(workLogRepository.findById(workLogId).get().getDate().toString(), Matchers.is(WORKLOG_DATE));
    }

    @Test
    public void aUserCanSearchForWorkLogs() throws Exception {
        //create a work log
        createAWorkLogItem(taskId);
        //post the work log
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                "/time-report/worklogs/search?startDate=" +
                        WORKLOG_DATE +
                        "&endDate=" +
                        WORKLOG_DATE,
                HttpMethod.GET,
                entity,
                String.class);
        assertThat(response.getStatusCode(), Matchers.is(OK));
        //verify that work log is added
        JsonArray jsonArray = new Gson().fromJson(response.getBody(), JsonArray.class);
        assertThat(jsonArray.size(), Matchers.is(1));
        assertThat(jsonArray.get(0).getAsJsonObject().get("hoursWorked").getAsInt(), Matchers.is(8));
        assertThat(jsonArray.get(0).getAsJsonObject().get("taskId").getAsString(), Matchers.is(taskId.toString()));
        assertThat(jsonArray.get(0).getAsJsonObject().get("date").getAsString(), Matchers.is(WORKLOG_DATE));
    }

    @Test
    public void aUserCanDeleteAWorkLog() throws Exception {
        //add a task
        ResponseEntity<String> response = createAWorkLogItem(taskId);
        HttpEntity<String> entity;
        HttpHeaders headers;

        assertThat(response.getStatusCode(), Matchers.is(CREATED));
        //verify get id from response body
        JsonObject responseBody = new Gson().fromJson(response.getBody(), JsonObject.class);
        UUID workLogId = UUID.fromString(responseBody.get("id").getAsString());
        //verify that work log is added
        assertTrue(workLogRepository.findById(workLogId).isPresent());
        //delete the work log
        headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        entity = new HttpEntity<>(headers);
        //call end point to delete work log
        ResponseEntity<String> response2 = restTemplate.exchange("/time-report/worklogs/"+workLogId.toString(), HttpMethod.DELETE, entity, String.class);
        assertThat(response2.getStatusCode(), Matchers.is(NO_CONTENT));
        //verify that work log is deleted
        assertTrue(workLogRepository.findById(workLogId).isEmpty());
    }

    @Test
    public void aUserCanUpdateAWorkLogItem() {
        //create a work log
        ResponseEntity<String> response = createAWorkLogItem(taskId);
        assertThat(response.getStatusCode(), Matchers.is(CREATED));
        //verify get id from response body
        JsonObject responseBody = new Gson().fromJson(response.getBody(), JsonObject.class);
        UUID workLogId = UUID.fromString(responseBody.get("id").getAsString());
        //verify that work log is added
        assertTrue(workLogRepository.findById(workLogId).isPresent());
        //update the work log
        JsonObject json = new JsonObject();
        json.addProperty("taskId", taskId.toString());
        json.addProperty("hoursWorked", 4);
        json.addProperty("date", WORKLOG_DATE);
        //post the work log
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);
        //call end point to update work log
        ResponseEntity<String> response2 = restTemplate.exchange("/time-report/worklogs/"+workLogId.toString(), HttpMethod.PUT, entity, String.class);
        assertThat(response2.getStatusCode(), Matchers.is(NO_CONTENT));
        //verify that work log is updated
        assertThat(workLogRepository.findById(workLogId).get().getHoursWorked(), Matchers.is(4));
    }

    @Test
    public void aUserCannotAddAWorkLogWithANonExistingTask() {
        //create a work log
        ResponseEntity<String> response = createAWorkLogItem(UUID.randomUUID());
        assertThat(response.getStatusCode(), Matchers.is(NOT_FOUND));
    }

    @Test
    public void aUserCannotUpdateANonExistingWorkLog() {
        //update the work log
        JsonObject json = new JsonObject();
        json.addProperty("taskId", taskId.toString());
        json.addProperty("hoursWorked", 4);
        json.addProperty("date", WORKLOG_DATE);
        //post the work log
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);
        //call end point to update work log
        ResponseEntity<String> response2 = restTemplate.exchange("/time-report/worklogs/"+UUID.randomUUID().toString(), HttpMethod.PUT, entity, String.class);
        assertThat(response2.getStatusCode(), Matchers.is(NOT_FOUND));
    }

    @Test
    public void aUserCannotDeleteANonExistingWorkLog() {
        //delete the work log
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        //call end point to delete work log
        ResponseEntity<String> response2 = restTemplate.exchange("/time-report/worklogs/"+UUID.randomUUID().toString(), HttpMethod.DELETE, entity, String.class);
        assertThat(response2.getStatusCode(), Matchers.is(NOT_FOUND));
    }

    @Test
    public void aUserCannotSearchForWorkLogsWhenNotLoggedIn() {
        //create a work log
        createAWorkLogItem(taskId);
        //post the work log
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                "/time-report/worklogs/search?startDate=" +
                        WORKLOG_DATE +
                        "&endDate=" +
                        WORKLOG_DATE,
                HttpMethod.GET,
                entity,
                String.class);
        assertThat(response.getStatusCode(), Matchers.is(UNAUTHORIZED));
    }

    private ResponseEntity<String> createAWorkLogItem(UUID taskId) {
        //create a work log
        JsonObject json = new JsonObject();
        json.addProperty("taskId", taskId.toString());
        json.addProperty("hoursWorked", 8);
        json.addProperty("date", WORKLOG_DATE);
        //post the work log
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);
        //call end point to add work log
        ResponseEntity<String> response = restTemplate.postForEntity("/time-report/worklogs", entity, String.class);
        return response;
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

        ResponseEntity<String> response = restTemplate.postForEntity("/time-report/users/customers", entity, String.class);
        // verify customer is added
        assertThat(response.getStatusCode(), Matchers.is(CREATED));
        // verify that cusetomer is added
        User user = userRepository.findById(USERNAME).get();
        return user.getCustomers().stream().filter(c -> c.getName().equals(customerName)).findFirst().get().getId();
    }

    private UUID addTaskToCustomer(UUID customerId, String title, String customerCode) {
        JsonObject json = new JsonObject();
        json.addProperty("title", title);
        json.addProperty("customerCode", customerCode);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/time-report/users/customers/"+customerId.toString()+ "/tasks", entity, String.class);
        assertThat(response.getStatusCode(), Matchers.is(CREATED));
        User user = userRepository.findById(USERNAME).get();
        //return the task matching the title
        return user.getCustomers().stream().filter(c -> c.getId().equals(customerId)).findFirst().get().getTasks().stream().filter(t -> t.getTitle().equals(title)).findFirst().get().getId();
    }

}
