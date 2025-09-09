package com.sap.refactoring.repository.impl;

import com.sap.refactoring.entity.User;
import com.sap.refactoring.repository.UserRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
@Profile("in_memory")
public class InMemoryUserRepository implements UserRepository {

  private final List<User> users = new CopyOnWriteArrayList<>();
  private final ConcurrentMap<String, UUID> emailIndex = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, UUID> nameIndex = new ConcurrentHashMap<>();

  @Override
  public User save(User user) {

    Optional<User> existingUser = findByEmail(user.getEmail());

    if (existingUser.isPresent()) {
      User existing = existingUser.get();
      existing.setName(user.getName());
      existing.setRoles(user.getRoles());
      return existing;
    } else {
      User userToSave = User.builder()
          .name(user.getName())
          .email(user.getEmail())
          .roles(user.getRoles())
          .build();

      userToSave.setId(UUID.randomUUID());
      users.add(userToSave);

      if (userToSave.getEmail() != null) {
        emailIndex.put(userToSave.getEmail(), userToSave.getId());
      }
      if (userToSave.getName() != null) {
        nameIndex.put(userToSave.getName(), userToSave.getId());
      }

      return userToSave;
    }
  }

  @Override
  public Optional<User> findByEmail(String email) {
    if (email == null) {
      return Optional.empty();
    }
    UUID userId = emailIndex.get(email);
    if (userId == null) {
      return Optional.empty();
    }
    return users.stream()
        .filter(user -> user.getId().equals(userId))
        .findFirst();
  }

  @Override
  public List<User> findAll() {
    return List.copyOf(users);
  }

  @Override
  public Page<User> findAll(Pageable pageable) {
    List<User> sortedUsers = new CopyOnWriteArrayList<>(users);

    if (pageable.getSort().isSorted()) {
      for (Sort.Order order : pageable.getSort()) {
        Comparator<User> comparator = switch (order.getProperty().toLowerCase()) {
          case "name" ->
              Comparator.comparing(User::getName, Comparator.nullsLast(Comparator.naturalOrder()));
          case "email" ->
              Comparator.comparing(User::getEmail, Comparator.nullsLast(Comparator.naturalOrder()));
          default -> Comparator.comparing(User::getId);
        };
        if (order.getDirection() == Sort.Direction.DESC) {
          comparator = comparator.reversed();
        }
        sortedUsers.sort(comparator);
      }
    }

    int total = sortedUsers.size();
    int start = Math.toIntExact(pageable.getOffset());
    int end = Math.min(start + pageable.getPageSize(), total);
    List<User> content = start >= total ? new ArrayList<>() : sortedUsers.subList(start, end);

    return new PageImpl<>(content, pageable, total);
  }

  @Override
  public void deleteByEmail(String email) {
    if (email == null) {
      return;
    }
    UUID userId = emailIndex.get(email);
    if (userId != null) {
      User userToRemove = users.stream()
          .filter(user -> user.getId().equals(userId))
          .findFirst()
          .orElse(null);

      if (userToRemove != null) {
        users.remove(userToRemove);
        emailIndex.remove(email);
        if (userToRemove.getName() != null) {
          nameIndex.remove(userToRemove.getName());
        }
      }
    }
  }

  @Override
  public boolean existsByEmail(String email) {
    return email != null && emailIndex.containsKey(email);
  }

  @Override
  public boolean existsByName(String name) {
    return name != null && nameIndex.containsKey(name);
  }
}