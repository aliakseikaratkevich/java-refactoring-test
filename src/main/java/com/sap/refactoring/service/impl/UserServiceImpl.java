package com.sap.refactoring.service.impl;

import com.sap.refactoring.dto.request.UserRequest;
import com.sap.refactoring.dto.response.UserResponse;
import com.sap.refactoring.entity.User;
import com.sap.refactoring.exception.EntityAlreadyExistsException;
import com.sap.refactoring.exception.EntityNotFoundException;
import com.sap.refactoring.mapper.UserMapper;
import com.sap.refactoring.repository.UserRepository;
import com.sap.refactoring.service.UserService;
import com.sap.refactoring.util.Constants;
import com.sap.refactoring.validation.UserValidator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final UserValidator userValidator;

  @Transactional
  public UserResponse createUser(UserRequest request) {
    User user = userMapper.toUser(request);
    userValidator.validate(user);
    if (userRepository.existsByName(user.getName())) {
      throw new EntityAlreadyExistsException(
          String.format(Constants.USER_ALREADY_EXISTS_BY_NAME_ERR_MSG, user.getName()));
    }
    if (userRepository.existsByEmail(user.getEmail())) {
      throw new EntityAlreadyExistsException(
          String.format(Constants.USER_ALREADY_EXISTS_BY_EMAIL_ERR_MSG, user.getEmail()));
    }
    User created = userRepository.save(user);
    return userMapper.toResponse(created);
  }

  @Transactional
  public UserResponse updateUser(UserRequest request) {
    User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new EntityNotFoundException(
            String.format(Constants.USER_NOT_FOUND_BY_EMAIL_ERR_MSG, request.email())));

    userMapper.updateUser(request, user);
    userValidator.validate(user);

    User updated = userRepository.save(user);
    return userMapper.toResponse(updated);
  }

  @Transactional
  public void deleteByEmail(String email) {
    if (!userRepository.existsByEmail(email)) {
      throw new EntityNotFoundException(
          String.format(Constants.USER_NOT_FOUND_BY_EMAIL_ERR_MSG, email));
    }
    userRepository.deleteByEmail(email);
  }

  @Transactional(readOnly = true)
  public List<UserResponse> getAllUsers() {
    return userRepository.findAll().stream()
        .map(userMapper::toResponse)
        .toList();
  }

  @Transactional(readOnly = true)
  public Page<UserResponse> getAllUsers(Pageable pageable) {
    return userRepository.findAll(pageable)
        .map(userMapper::toResponse);
  }

  @Transactional(readOnly = true)
  public UserResponse getByEmail(String email) {
    return userRepository.findByEmail(email)
        .map(userMapper::toResponse)
        .orElseThrow(() -> new EntityNotFoundException(
            String.format(Constants.USER_NOT_FOUND_BY_EMAIL_ERR_MSG, email)));
  }
}