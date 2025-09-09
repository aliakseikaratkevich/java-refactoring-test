package com.sap.refactoring.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sap.refactoring.dto.request.UserRequest;
import com.sap.refactoring.dto.response.UserResponse;
import com.sap.refactoring.entity.User;
import com.sap.refactoring.integration.util.TestDataLoader;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


@DisplayName("UserController InMemory Integration Tests")
class UserControllerIntegrationTest extends BaseIntegrationTest {

  @Autowired
  private TestDataLoader testDataLoader;

  @Nested
  @DisplayName("Create User Operations")
  class CreateUserOperations {

    @Test
    @DisplayName("should create user successfully when valid data provided")
    void shouldCreateUserSuccessfully_whenValidDataProvided() throws Exception {
      // Given
      UserRequest validUser = new UserRequest("Test User", "test@example.com", List.of("USER"));

      // When & Then
      String responseJson = mockMvc.perform(post("/api/v1/users")
              .contentType(APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(validUser)))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.name").value("Test User"))
          .andExpect(jsonPath("$.email").value("test@example.com"))
          .andExpect(jsonPath("$.roles").isArray())
          .andReturn()
          .getResponse()
          .getContentAsString();

      UserResponse response = objectMapper.readValue(responseJson, UserResponse.class);
      assertThat(response.name()).isEqualTo("Test User");
      assertThat(response.email()).isEqualTo("test@example.com");
      assertThat(response.roles()).containsExactly("USER");

      assertThat(userExists("test@example.com")).isTrue();

      User savedUser = userRepository.findByEmail("test@example.com").orElse(null);
      assertThat(savedUser).isNotNull();
      assertThat(savedUser.getRoles()).containsExactly("USER");
    }

    @Test
    @DisplayName("should create multiple users from JSON file successfully")
    void shouldCreateMultipleUsersFromJsonFileSuccessfully() throws Exception {
      // Given
      List<UserRequest> validUsers = testDataLoader.loadValidUsers();

      // When & Then
      for (UserRequest userRequest : validUsers) {
        mockMvc.perform(post("/api/v1/users")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value(userRequest.name()))
            .andExpect(jsonPath("$.email").value(userRequest.email()))
            .andExpect(jsonPath("$.roles").isArray());
      }

      assertThat(getUserCount()).isEqualTo(validUsers.size());
    }

    @Test
    @DisplayName("should return bad request when invalid user data provided")
    void shouldReturnBadRequest_whenInvalidUserDataProvided() throws Exception {
      // Given
      List<UserRequest> invalidUsers = testDataLoader.loadInvalidUsers();

      // When & Then
      for (UserRequest invalidUser : invalidUsers) {
        System.out.println(
            "Testing invalid user: " + invalidUser.name() + " with email: " + invalidUser.email()
                + " and roles: " + invalidUser.roles());

        mockMvc.perform(post("/api/v1/users")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
            .andExpect(status().isBadRequest());
      }
    }

    @Test
    @DisplayName("should return conflict when user with duplicate email")
    void shouldReturnConflict_whenUserWithDuplicateEmail() throws Exception {
      // Given
      UserRequest firstUser = new UserRequest("First User", "duplicate@example.com",
          List.of("USER"));
      UserRequest secondUser = new UserRequest("Second User", "duplicate@example.com",
          List.of("ADMIN"));

      mockMvc.perform(post("/api/v1/users")
              .contentType(APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(firstUser)))
          .andExpect(status().isCreated());

      mockMvc.perform(post("/api/v1/users")
              .contentType(APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(secondUser)))
          .andExpect(status().isConflict());
    }
  }

  @Nested
  @DisplayName("Get User Operations")
  class GetUserOperations {

    @Test
    @DisplayName("should return user when finding by existing email")
    void shouldReturnUser_whenFindingByExistingEmail() throws Exception {
      // Given
      UserRequest userRequest = new UserRequest("Test User", "find@example.com", List.of("USER"));
      createTestUser(userRequest.name(), userRequest.email(), userRequest.roles());

      // When & Then
      mockMvc.perform(get("/api/v1/users/find@example.com"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.name").value("Test User"))
          .andExpect(jsonPath("$.email").value("find@example.com"))
          .andExpect(jsonPath("$.roles").isArray());
    }

    @Test
    @DisplayName("should return not found when user email does not exist")
    void shouldReturnNotFound_whenUserEmailDoesNotExist() throws Exception {
      // When & Then
      mockMvc.perform(get("/api/v1/users/find/nonexistent@example.com"))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should return all users when calling getAllUsers")
    void shouldReturnAllUsers_whenCallingGetAllUsers() throws Exception {
      // Given
      List<UserRequest> users = testDataLoader.loadValidUsers();
      createTestUsers(users);

      // When & Then
      String responseJson = mockMvc.perform(get("/api/v1/users"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$").isArray())
          .andExpect(jsonPath("$.length()").value(users.size()))
          .andReturn()
          .getResponse()
          .getContentAsString();

      List<UserResponse> response = objectMapper.readValue(responseJson,
          new TypeReference<List<UserResponse>>() {
          });
      assertThat(response).hasSize(users.size());
    }
  }

  @Nested
  @DisplayName("Update User Operations")
  class UpdateUserOperations {

    @Test
    @DisplayName("should update user successfully when valid data provided")
    void shouldUpdateUserSuccessfully_whenValidDataProvided() throws Exception {
      // Given
      UserRequest originalUser = new UserRequest("Original Name", "update@example.com",
          List.of("USER"));
      User createdUser = createTestUser(originalUser.name(), originalUser.email(),
          originalUser.roles());

      assertThat(userExists("update@example.com")).isTrue();
      assertThat(createdUser.getEmail()).isEqualTo("update@example.com");

      UserRequest updatedUser = new UserRequest("Updated Name", "update@example.com",
          List.of("USER"));

      // When & Then
      mockMvc.perform(put("/api/v1/users")
              .contentType(APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(updatedUser)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.name").value("Updated Name"))
          .andExpect(jsonPath("$.email").value("update@example.com"))
          .andExpect(jsonPath("$.roles").isArray());
    }
  }

  @Nested
  @DisplayName("Delete User Operations")
  class DeleteUserOperations {

    @Test
    @DisplayName("should delete user successfully when email exists")
    void shouldDeleteUserSuccessfully_whenEmailExists() throws Exception {
      // Given
      UserRequest userRequest = new UserRequest("Delete User", "delete@example.com",
          List.of("USER"));
      createTestUser(userRequest.name(), userRequest.email(), userRequest.roles());

      assertThat(userExists("delete@example.com")).isTrue();

      // When & Then
      mockMvc.perform(delete("/api/v1/users/delete@example.com"))
          .andExpect(status().isNoContent());

      assertThat(userExists("delete@example.com")).isFalse();
    }

    @Test
    @DisplayName("should return not found when deleting non-existent user")
    void shouldReturnNotFound_whenDeletingNonExistentUser() throws Exception {
      // When & Then
      mockMvc.perform(delete("/api/v1/users/nonexistent@example.com"))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("Error Handling")
  class ErrorHandling {

    @Test
    @DisplayName("should return bad request when invalid JSON provided")
    void shouldReturnBadRequest_whenInvalidJsonProvided() throws Exception {
      // When & Then
      mockMvc.perform(post("/api/v1/users")
              .contentType(APPLICATION_JSON)
              .content("invalid json content"))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return method not allowed for unsupported HTTP methods")
    void shouldReturnMethodNotAllowed_forUnsupportedHttpMethods() throws Exception {
      // When & Then
      mockMvc.perform(patch("/api/v1/users")
              .contentType(APPLICATION_JSON)
              .content("{}"))
          .andExpect(status().isMethodNotAllowed());
    }
  }
}
