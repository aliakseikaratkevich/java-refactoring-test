package com.sap.refactoring.validation;

import com.sap.refactoring.entity.User;
import com.sap.refactoring.exception.InvalidUserRolesException;
import com.sap.refactoring.util.Constants;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {

  public void validate(User user) {
    if (user == null) {
      throw new InvalidUserRolesException("User cannot be null");
    }
    validateRoles(user.getRoles());
  }

  private void validateRoles(List<String> roles) {
    if (roles == null || roles.isEmpty()) {
      throw new InvalidUserRolesException(Constants.USER_MUST_HAVE_AT_LEAST_ONE_ROLE_ERR_MSG);
    }
  }
}


