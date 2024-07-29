package pb.se.TimeReportService.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.hamcrest.Matchers;
import org.jetbrains.annotations.NotNull;
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

import java.util.Optional;
import java.util.UUID;

import static com.mongodb.assertions.Assertions.assertNull;
import static com.mongodb.assertions.Assertions.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AuthControllerTest {


    public static final String USERNAME = "baba";
    public static final String THE_PASSWORD = "säkert";
    @Autowired
    UserRepository userRepository;
    @Autowired
    WhiteListedUserRepository whiteListedUserRepository;
    @Autowired
    TestRestTemplate restTemplate;
    private String accessToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        whiteListedUserRepository.deleteAll();

        whiteListedUserRepository.save(new WhiteListedUser(USERNAME));

        signUp(USERNAME, THE_PASSWORD);
    }


    @Test
    public void userThatAreSignOnCanSignIn() throws Exception {
        // signin
        ResponseEntity<String> response = signIn();

        JsonObject responseBody= new Gson().fromJson(response.getBody(), JsonObject.class);
        assertTrue(responseBody.has("accessToken"));
    }

    @Test
    public void userThatAreSignCanAlsoSelfSignOff() throws Exception {
        // signin
        ResponseEntity<String> response = signIn();

        JsonObject responseBody= new Gson().fromJson(response.getBody(), JsonObject.class);
        String accessToken = responseBody.get("accessToken").getAsString();

        //Sign off
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response2 = restTemplate.exchange("/auth/signdown", HttpMethod.DELETE, entity, String.class);
        assertThat(response2.getStatusCode(), Matchers.is(NO_CONTENT));

        //verify that the user is deleted
        Optional<User> userRepositoryById = userRepository.findById(USERNAME);
        assertTrue(userRepositoryById.isEmpty());
    }

    @Test
    public void userThatAreSignOnCanNotSignInWithWrongPassword() throws Exception {
        // signin
        JsonObject json = new JsonObject();
        json.addProperty("username", USERNAME);
        json.addProperty("password", "wrong");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity( "/auth/signin", entity, String.class);
        assertThat(response.getStatusCode(), Matchers.is(UNAUTHORIZED));
    }

    @Test
    public void aUserCannotSignUpIfNotWhiteListedItReturnForbidden() {
        whiteListedUserRepository.deleteAll();
        // signup
        JsonObject json = new JsonObject();
        json.addProperty("username", "notWhiteListed");
        json.addProperty("password", "password");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity( "/auth/signup", entity, String.class);

        assertThat(response.getStatusCode(), Matchers.is(FORBIDDEN));
    }

    @Test
    public void aUserCannotSignUpIfAlreadyExistsNothingIsLeadedInResponse() {
        // signup
        JsonObject json = new JsonObject();
        json.addProperty("username", USERNAME);
        json.addProperty("password", "password");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity( "/auth/signup", entity, String.class);

        assertThat(response.getStatusCode(), Matchers.is(CONFLICT));
        assertNull(response.getBody());
    }

    @NotNull
    private ResponseEntity<String> signIn() {
        JsonObject json = new JsonObject();
        json.addProperty("username", USERNAME);
        json.addProperty("password", THE_PASSWORD);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity( "/auth/signin", entity, String.class);
        assertThat(response.getStatusCode(), Matchers.is(OK));
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
