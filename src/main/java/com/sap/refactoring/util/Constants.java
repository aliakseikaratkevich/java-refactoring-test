package com.sap.refactoring.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class Constants {

  // user
  public static final String USER_ALREADY_EXISTS_BY_NAME_ERR_MSG = "User with username %s already exists";
  public static final String USER_ALREADY_EXISTS_BY_EMAIL_ERR_MSG = "User with email %s already exists";
  public static final String USER_NOT_FOUND_BY_EMAIL_ERR_MSG = "User with email %s not found";
  public static final String USER_MUST_HAVE_AT_LEAST_ONE_ROLE_ERR_MSG = "User must have at least one role";
}