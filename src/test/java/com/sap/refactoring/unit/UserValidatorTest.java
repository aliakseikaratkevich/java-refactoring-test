package com.sap.refactoring.unit;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sap.refactoring.entity.User;
import com.sap.refactoring.exception.InvalidUserRolesException;
import com.sap.refactoring.validation.UserValidator;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("UserValidator")
class UserValidatorTest {

  private UserValidator validator;
  private User validUser;

  @BeforeEach
  void setUp() {
    validator = new UserValidator();
    validUser = User.builder()
        .name("John Doe")
        .email("john@example.com")
        .roles(List.of("USER", "ADMIN"))
        .build();
  }

  @Test
  @DisplayName("should pass validation for user with valid data")
  void shouldPassValidation_forUserWithValidData() {
    // When & Then
    validator.validate(validUser);
    // No exception should be thrown
  }

  @Test
  @DisplayName("should pass validation for user with empty roles list")
  void shouldPassValidation_forUserWithEmptyRolesList() {
    // Given
    User userWithEmptyRoles = User.builder()
        .name("No Roles")
        .email("noroles@example.com")
        .roles(List.of())
        .build();

    // When & Then
    assertThatThrownBy(() -> validator.validate(userWithEmptyRoles))
        .isInstanceOf(InvalidUserRolesException.class);
  }

  @Test
  @DisplayName("should accept all valid roles")
  void shouldAcceptAllValidRoles() {
    // Given
    List<String> validRoles = List.of("USER", "ADMIN", "MODERATOR", "GUEST");

    // When & Then
    for (String role : validRoles) {
      User userWithRole = User.builder()
          .name("Test User")
          .email("test@example.com")
          .roles(List.of(role))
          .build();

      validator.validate(userWithRole);
    }
  }

  @Test
  @DisplayName("should throw InvalidUserRolesException when user is null")
  void shouldThrowInvalidUserRolesException_whenUserIsNull() {
    // When & Then
    assertThatThrownBy(() -> validator.validate(null))
        .isInstanceOf(InvalidUserRolesException.class)
        .hasMessageContaining("User cannot be null");
  }
}
