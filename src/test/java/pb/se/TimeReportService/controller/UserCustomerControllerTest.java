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
import pb.se.TimeReportService.port.persistence.UserRepository;
import pb.se.TimeReportService.port.persistence.WhiteListedUserRepository;

import java.util.Optional;

import static com.mongodb.assertions.Assertions.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.HttpStatus.CREATED;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserCustomerControllerTest {


    public static final String USERNAME = "baba";
    @Autowired
    UserRepository userRepository;
    @Autowired
    WhiteListedUserRepository whiteListedUserRepository;
    @Autowired
    TestRestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        whiteListedUserRepository.deleteAll();

        whiteListedUserRepository.save(new WhiteListedUser(USERNAME));
    }

    @Test
    public void canSignUpAndAddCustomerToUser() throws Exception {

        String accessToken = signUp(USERNAME, "säkert");

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
}
