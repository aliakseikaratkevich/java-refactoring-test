package com.sap.refactoring.controller;

import com.sap.refactoring.dto.request.PageRequest;
import com.sap.refactoring.dto.request.UserRequest;
import com.sap.refactoring.dto.response.UserResponse;
import com.sap.refactoring.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

  private final UserService userService;

  @PostMapping
  public ResponseEntity<UserResponse> addUser(@Valid @RequestBody UserRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
  }

  @PutMapping
  public ResponseEntity<UserResponse> updateUser(@Valid @RequestBody UserRequest request) {
    return ResponseEntity.ok(userService.updateUser(request));
  }

  @DeleteMapping("/{email}")
  public ResponseEntity<Void> deleteUserByEmail(@PathVariable("email") String email) {
    userService.deleteByEmail(email);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  public ResponseEntity<List<UserResponse>> getUsers() {
    return ResponseEntity.ok(userService.getAllUsers());
  }

  @GetMapping(params = {"page", "size", "sortBy", "sortDirection"})
  public ResponseEntity<Page<UserResponse>> getUsersPaginated(@Valid PageRequest pageRequest) {
    Page<UserResponse> response = userService.getAllUsers(pageRequest.toPageable());
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{email}")
  public ResponseEntity<UserResponse> findUserByEmail(@PathVariable("email") String email) {
    return ResponseEntity.ok(userService.getByEmail(email));
  }
}