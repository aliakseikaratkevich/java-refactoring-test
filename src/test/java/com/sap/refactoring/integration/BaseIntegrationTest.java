package com.sap.refactoring.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.refactoring.dto.request.UserRequest;
import com.sap.refactoring.dto.response.UserResponse;
import com.sap.refactoring.entity.User;
import com.sap.refactoring.repository.UserRepository;
import com.sap.refactoring.service.impl.UserServiceImpl;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ActiveProfiles("in_memory")
public abstract class BaseIntegrationTest {

  @Autowired
  protected WebApplicationContext webApplicationContext;

  @Autowired
  protected ObjectMapper objectMapper;

  @Autowired
  protected UserServiceImpl userService;

  @Autowired
  protected UserRepository userRepository;

  protected MockMvc mockMvc;

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
}
