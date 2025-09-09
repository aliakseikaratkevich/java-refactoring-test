package com.sap.refactoring.exception.handler;

import com.sap.refactoring.dto.response.ErrorResponse;
import com.sap.refactoring.dto.response.ValidationErrorResponse;
import com.sap.refactoring.exception.EntityAlreadyExistsException;
import com.sap.refactoring.exception.EntityNotFoundException;
import com.sap.refactoring.exception.InvalidUserRolesException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ValidationErrorResponse> handleValidation(
      MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName =
          error instanceof FieldError ? ((FieldError) error).getField() : error.getObjectName();
      errors.put(fieldName, error.getDefaultMessage());
    });
    ValidationErrorResponse body = new ValidationErrorResponse("Validation failed",
        HttpStatus.BAD_REQUEST.value(), System.currentTimeMillis(), errors);
    return ResponseEntity.badRequest().body(body);
  }

  @ExceptionHandler(EntityAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleEmailExists(RuntimeException ex) {
    ErrorResponse body = new ErrorResponse(ex.getMessage(), HttpStatus.CONFLICT.value(),
        System.currentTimeMillis());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
  }

  @ExceptionHandler(InvalidUserRolesException.class)
  public ResponseEntity<ErrorResponse> handleInvalidRoles(InvalidUserRolesException ex) {
    ErrorResponse body = new ErrorResponse(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY.value(),
        System.currentTimeMillis());
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex) {
    ErrorResponse body = new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value(),
        System.currentTimeMillis());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
  }
}


