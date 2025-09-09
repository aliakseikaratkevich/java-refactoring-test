package com.sap.refactoring.dto.response;

import java.util.Map;

public record ValidationErrorResponse(String message, int status, long timestamp,
                                      Map<String, String> errors) {

}


