package com.sap.refactoring.service;

import com.sap.refactoring.dto.request.UserRequest;
import com.sap.refactoring.dto.response.UserResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

  UserResponse createUser(UserRequest request);

  UserResponse updateUser(UserRequest request);

  void deleteByEmail(String email);

  List<UserResponse> getAllUsers();

  Page<UserResponse> getAllUsers(Pageable pageable);

  UserResponse getByEmail(String email);
}
