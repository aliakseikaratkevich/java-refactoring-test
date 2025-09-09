package com.sap.refactoring.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.refactoring.dto.request.UserRequest;
import com.sap.refactoring.dto.response.UserResponse;
import com.sap.refactoring.entity.User;
import com.sap.refactoring.repository.UserRepository;
import com.sap.refactoring.service.impl.UserServiceImpl;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@Transactional
@ActiveProfiles("testcontainers")
public abstract class BaseTestcontainersIntegrationTest {

  @Container
  public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");
  @Autowired
  protected WebApplicationContext webApplicationContext;
  @Autowired
  protected ObjectMapper objectMapper;
  @Autowired
  protected UserServiceImpl userService;
  @Autowired
  protected UserRepository userRepository;
  protected MockMvc mockMvc;

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    clearTestData();
  }

  protected void clearTestData() {
    List<User> allUsers = userRepository.findAll();
    for (User user : allUsers) {
      if (user.getEmail() != null) {
        userRepository.deleteByEmail(user.getEmail());
      }
    }
  }

  protected User createTestUser(String name, String email, List<String> roles) {
    UserRequest userRequest = new UserRequest(name, email, roles);
    UserResponse response = userService.createUser(userRequest);

    return userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("Failed to create test user: " + email));
  }

  protected void createTestUsers(List<UserRequest> userRequests) {
    userRequests
        .forEach(request -> createTestUser(request.name(), request.email(), request.roles()));
  }

  protected boolean userExists(String email) {
    return userRepository.existsByEmail(email);
  }

  protected long getUserCount() {
    return userRepository.findAll().size();
  }

  protected String generateUniqueEmail() {
    return "test-" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
  }
}
