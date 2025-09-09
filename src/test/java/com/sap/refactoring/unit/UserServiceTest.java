package com.sap.refactoring.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sap.refactoring.dto.request.UserRequest;
import com.sap.refactoring.dto.response.UserResponse;
import com.sap.refactoring.entity.User;
import com.sap.refactoring.exception.EntityAlreadyExistsException;
import com.sap.refactoring.exception.EntityNotFoundException;
import com.sap.refactoring.mapper.UserMapper;
import com.sap.refactoring.repository.UserRepository;
import com.sap.refactoring.service.impl.UserServiceImpl;
import com.sap.refactoring.validation.UserValidator;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService")
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMapper userMapper;

  @Mock
  private UserValidator userValidator;

  @InjectMocks
  private UserServiceImpl userService;

  private UserRequest validUserRequest;
  private User validUser;
  private UserResponse validUserResponse;

  @BeforeEach
  void setUp() {
    validUserRequest = new UserRequest("John Doe", "john@example.com", List.of("USER", "ADMIN"));
    validUser = User.builder()
        .id(java.util.UUID.randomUUID())
        .name("John Doe")
        .email("john@example.com")
        .roles(List.of("USER", "ADMIN"))
        .build();
    validUserResponse = new UserResponse("John Doe", "john@example.com", List.of("USER", "ADMIN"));
  }

  @Test
  @DisplayName("should create user successfully when all validations pass")
  void shouldCreateUserSuccessfully_whenAllValidationsPass() {
    // Given
    when(userMapper.toUser(validUserRequest)).thenReturn(validUser);
    when(userRepository.existsByName("John Doe")).thenReturn(false);
    when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
    when(userRepository.save(validUser)).thenReturn(validUser);
    when(userMapper.toResponse(validUser)).thenReturn(validUserResponse);

    // When
    UserResponse result = userService.createUser(validUserRequest);

    // Then
    assertThat(result).isEqualTo(validUserResponse);
    verify(userValidator).validate(validUser);
    verify(userRepository).save(validUser);
  }

  @Test
  @DisplayName("should throw EntityAlreadyExistsException when user with same email exists")
  void shouldThrowEntityAlreadyExistsException_whenUserWithSameEmailExists() {
    // Given
    when(userMapper.toUser(validUserRequest)).thenReturn(validUser);
    when(userRepository.existsByName("John Doe")).thenReturn(false);
    when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

    // When & Then
    assertThatThrownBy(() -> userService.createUser(validUserRequest))
        .isInstanceOf(EntityAlreadyExistsException.class)
        .hasMessageContaining("john@example.com");

    verify(userRepository, never()).save(any());
  }

  @Test
  @DisplayName("should update user successfully when user exists")
  void shouldUpdateUserSuccessfully_whenUserExists() {
    // Given
    when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(validUser));
    when(userRepository.save(any(User.class))).thenReturn(validUser);
    when(userMapper.toResponse(any(User.class))).thenReturn(validUserResponse);

    // When
    UserResponse result = userService.updateUser(validUserRequest);

    // Then
    assertThat(result).isEqualTo(validUserResponse);
    verify(userValidator).validate(validUser);
    verify(userRepository).save(validUser);
  }

  @Test
  @DisplayName("should return user when finding by existing email")
  void shouldReturnUser_whenFindingByExistingEmail() {
    // Given
    String email = "john@example.com";
    when(userRepository.findByEmail(email)).thenReturn(Optional.of(validUser));
    when(userMapper.toResponse(validUser)).thenReturn(validUserResponse);

    // When
    UserResponse result = userService.getByEmail(email);

    // Then
    assertThat(result).isEqualTo(validUserResponse);
    verify(userRepository).findByEmail(email);
  }

  @Test
  @DisplayName("should throw EntityNotFoundException when user with email does not exist")
  void shouldThrowEntityNotFoundException_whenUserWithEmailDoesNotExist() {
    // Given
    String email = "nonexistent@example.com";
    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> userService.getByEmail(email))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining(email);
  }
}
