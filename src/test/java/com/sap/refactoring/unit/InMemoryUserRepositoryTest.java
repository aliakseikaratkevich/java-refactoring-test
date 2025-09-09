package com.sap.refactoring.unit;

import static org.assertj.core.api.Assertions.assertThat;

import com.sap.refactoring.entity.User;
import com.sap.refactoring.repository.impl.InMemoryUserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("InMemoryUserRepository")
class InMemoryUserRepositoryTest {

  private InMemoryUserRepository repository;
  private User validUser;
  private User anotherUser;

  @BeforeEach
  void setUp() {
    repository = new InMemoryUserRepository();

    validUser = User.builder()
        .name("John Doe")
        .email("john@example.com")
        .roles(List.of("USER", "ADMIN"))
        .build();

    anotherUser = User.builder()
        .name("Jane Smith")
        .email("jane@example.com")
        .roles(List.of("USER"))
        .build();
  }

  @Test
  @DisplayName("should create new user with generated UUID when saving valid user")
  void shouldCreateNewUserWithGeneratedUUID_whenSavingValidUser() {
    // When
    User savedUser = repository.save(validUser);

    // Then
    assertThat(savedUser.getId()).isNotNull();
    assertThat(savedUser.getName()).isEqualTo("John Doe");
    assertThat(savedUser.getEmail()).isEqualTo("john@example.com");
    assertThat(savedUser.getRoles()).containsExactly("USER", "ADMIN");
  }

  @Test
  @DisplayName("should update indexes when saving user")
  void shouldUpdateIndexes_whenSavingUser() {
    // When
    repository.save(validUser);

    // Then
    assertThat(repository.existsByEmail("john@example.com")).isTrue();
    assertThat(repository.existsByName("John Doe")).isTrue();
  }

  @Test
  @DisplayName("should return user when finding by existing email")
  void shouldReturnUser_whenFindingByExistingEmail() {
    // Given
    User savedUser = repository.save(validUser);

    // When
    Optional<User> foundUser = repository.findByEmail("john@example.com");

    // Then
    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getId()).isEqualTo(savedUser.getId());
    assertThat(foundUser.get().getName()).isEqualTo("John Doe");
  }

  @Test
  @DisplayName("should return empty optional when finding by non-existing email")
  void shouldReturnEmptyOptional_whenFindingByNonExistingEmail() {
    // When
    Optional<User> foundUser = repository.findByEmail("nonexistent@example.com");

    // Then
    assertThat(foundUser).isEmpty();
  }

  @Test
  @DisplayName("should return all users when calling findAll")
  void shouldReturnAllUsers_whenCallingFindAll() {
    // Given
    repository.save(validUser);
    repository.save(anotherUser);

    // When
    List<User> allUsers = repository.findAll();

    // Then
    assertThat(allUsers).hasSize(2);
    assertThat(allUsers).extracting("email")
        .containsExactlyInAnyOrder("john@example.com", "jane@example.com");
  }

  @Test
  @DisplayName("should remove user and cleanup indexes when deleting by existing email")
  void shouldRemoveUserAndCleanupIndexes_whenDeletingByExistingEmail() {
    // Given
    repository.save(validUser);
    repository.save(anotherUser);

    // When
    repository.deleteByEmail("john@example.com");

    // Then
    assertThat(repository.findAll()).hasSize(1);
    assertThat(repository.existsByEmail("john@example.com")).isFalse();
    assertThat(repository.existsByName("John Doe")).isFalse();
  }

  @Test
  @DisplayName("should handle null values gracefully")
  void shouldHandleNullValuesGracefully() {
    // Given
    User userWithNullEmail = User.builder()
        .name("No Email")
        .email(null)
        .roles(List.of("USER"))
        .build();

    // When
    repository.save(userWithNullEmail);

    // Then
    assertThat(repository.existsByEmail(null)).isFalse();
    assertThat(repository.findByEmail(null)).isEmpty();
  }
}
