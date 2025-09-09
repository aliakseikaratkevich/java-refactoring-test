package com.sap.refactoring.repository.impl;

import com.sap.refactoring.entity.User;
import com.sap.refactoring.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

@Profile("postgres")
public interface PostgresUserRepository extends JpaRepository<User, UUID>, UserRepository {

  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);

  boolean existsByName(String name);
}


