package pb.se.TimeReportService.controller;

import com.google.gson.JsonObject;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import pb.se.TimeReportService.domain.User;
import pb.se.TimeReportService.port.persistence.UserRepository;

import java.util.Optional;

import static com.mongodb.assertions.Assertions.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static pb.se.TimeReportService.controller.CustomerController.BABA;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserControllerTest {


    @Autowired
    UserRepository userRepository;
    @Autowired
    TestRestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    public void canAddCustomersToUser() throws Exception {
        // Create a user
        User user = new User(BABA);
        userRepository.save(user);

        Optional<User> userRepositoryById = userRepository.findById(user.getUsername());
        assertTrue(userRepositoryById.isPresent());

        // Add a customer to user by rest call
        JsonObject json = new JsonObject();
        json.addProperty("name", "SHB");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity( "/time-report/customers", entity, String.class);

        assertThat(response.getStatusCode(), Matchers.is(CREATED));

        // Check that the customer is added to the user
        User userWithCustomers = userRepository.findById(user.getUsername()).get();
        assertThat(userWithCustomers.getCustomers(), hasSize(1));

    }
}
