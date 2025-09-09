package com.sap.refactoring.repository;

import com.sap.refactoring.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepository {

  User save(User user);

  Optional<User> findByEmail(String email);

  List<User> findAll();

  Page<User> findAll(Pageable pageable);

  void deleteByEmail(String email);

  boolean existsByEmail(String email);

  boolean existsByName(String name);
}


