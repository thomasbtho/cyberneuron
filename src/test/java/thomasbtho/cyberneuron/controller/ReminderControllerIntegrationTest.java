package thomasbtho.cyberneuron.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import thomasbtho.cyberneuron.dto.LoginRequest;
import thomasbtho.cyberneuron.dto.ReminderPatchRequest;
import thomasbtho.cyberneuron.dto.ReminderRequest;
import thomasbtho.cyberneuron.entity.ReminderStatus;
import thomasbtho.cyberneuron.entity.User;
import thomasbtho.cyberneuron.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
class ReminderControllerIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private String authToken;
    private User testUser;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    private static final String TEST_USER_EMAIL = "testuser@example.com";
    private static final String TEST_USER_PASSWORD = "Password123!";
    private static final String TEST_USER_DISPLAY_NAME = "Test User";

    @BeforeEach
    void setUp() throws Exception {
        // Clean up any existing test data
        userRepository.deleteAll();

        // Create and save test user
        testUser = createTestUser(TEST_USER_EMAIL, TEST_USER_PASSWORD, TEST_USER_DISPLAY_NAME);

        // Authenticate and get token
        authToken = authenticateUser(TEST_USER_EMAIL, TEST_USER_PASSWORD);
    }

    @AfterEach
    void tearDown() {
        // Clean up after each test
        userRepository.deleteAll();
    }

    private User createTestUser(String email, String password, String displayName) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setDisplayName(displayName);
        user.setRoles(Set.of("ROLE_USER"));
        return userRepository.saveAndFlush(user);
    }

    private String authenticateUser(String email, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest(email, password);
        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(loginResponse)
                .get("token")
                .asText();
    }

    private String getAuthHeader() {
        return "Bearer " + authToken;
    }

    private ReminderRequest createTestReminderRequest() {
        Date futureDate = new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7));
        return new ReminderRequest("Test Reminder", futureDate);
    }

    @Test
    void createReminder_WithValidData_ShouldReturnCreated() throws Exception {
        ReminderRequest request = createTestReminderRequest();

        mockMvc.perform(post("/api/users/{userId}/reminders", testUser.getId())
                        .header("Authorization", getAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(request.title()))
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    String responseDateStr = JsonPath.read(json, "$.deadline");
                    ZonedDateTime responseDate = ZonedDateTime.parse(responseDateStr);
                    Instant expectedInstant = request.deadline().toInstant();
                    assertEquals(expectedInstant, responseDate.toInstant());
                })
//                .andExpect(jsonPath("$.deadline").value(request.deadline().toInstant().atZone(ZoneId.of("UTC")).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)))
                .andExpect(jsonPath("$.status").value(ReminderStatus.SCHEDULED.toString()));
    }

    @Test
    void getReminders_ShouldReturnUserReminders() throws Exception {
        // First create a reminder
        ReminderRequest request = createTestReminderRequest();
        mockMvc.perform(post("/api/users/{userId}/reminders", testUser.getId())
                .header("Authorization", getAuthHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Then get all reminders
        mockMvc.perform(get("/api/users/{userId}/reminders", testUser.getId())
                        .header("Authorization", getAuthHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[0].title").value(request.title()));
    }

    @Test
    void updateReminder_WithValidData_ShouldReturnUpdatedReminder() throws Exception {
        // First create a reminder
        ReminderRequest createRequest = createTestReminderRequest();
        String createResponse = mockMvc.perform(post("/api/users/{userId}/reminders", testUser.getId())
                        .header("Authorization", getAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andReturn().getResponse().getContentAsString();

        Long reminderId = objectMapper.readTree(createResponse).get("id").asLong();

        // Then update the reminder
        ReminderPatchRequest updateRequest = new ReminderPatchRequest("Updated Title", new Date(), null);

        mockMvc.perform(patch("/api/users/{userId}/reminders/{id}", testUser.getId(), reminderId)
                        .header("Authorization", getAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(updateRequest.getTitle()))
                .andExpect(jsonPath("$.status").value(ReminderStatus.SCHEDULED.toString()));
    }

    @Test
    void deleteReminder_WithValidId_ShouldReturnNoContent() throws Exception {
        // First create a reminder
        ReminderRequest createRequest = createTestReminderRequest();
        String createResponse = mockMvc.perform(post("/api/users/{userId}/reminders", testUser.getId())
                        .header("Authorization", getAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andReturn().getResponse().getContentAsString();

        Long reminderId = objectMapper.readTree(createResponse).get("id").asLong();

        // Then delete the reminder
        mockMvc.perform(delete("/api/users/{userId}/reminders/{id}", testUser.getId(), reminderId)
                        .header("Authorization", getAuthHeader()))
                .andExpect(status().isNoContent());

        // Verify the reminder was deleted
        mockMvc.perform(get("/api/users/{userId}/reminders", testUser.getId())
                        .header("Authorization", getAuthHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void accessOtherUserReminders_ShouldBeForbidden() throws Exception {
        // Create another test user
        User otherUser = createTestUser("otheruser@example.com", "OtherPass123!", "Other User");

        // Try to access other user's reminders
        mockMvc.perform(get("/api/users/{userId}/reminders", otherUser.getId())
                        .header("Authorization", getAuthHeader()))
                .andExpect(status().isForbidden());
    }
}
