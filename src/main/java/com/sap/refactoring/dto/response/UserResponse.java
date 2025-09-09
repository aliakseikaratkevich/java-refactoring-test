package com.sap.refactoring.dto.response;

import java.util.List;

public record UserResponse(String name, String email, List<String> roles) {

}


