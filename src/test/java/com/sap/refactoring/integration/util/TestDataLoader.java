package com.sap.refactoring.integration.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.refactoring.dto.request.UserRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class TestDataLoader {

  private final ObjectMapper objectMapper;

  public TestDataLoader(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public List<UserRequest> loadValidUsers() throws IOException {
    return loadFromResource("test-data/valid-users.json", new TypeReference<>() {
    });
  }

  public List<UserRequest> loadInvalidUsers() throws IOException {
    return loadFromResource("test-data/invalid-users.json", new TypeReference<>() {
    });
  }

  private <T> T loadFromResource(String resourcePath, TypeReference<T> typeReference)
      throws IOException {
    ClassPathResource resource = new ClassPathResource(resourcePath);
    try (InputStream inputStream = resource.getInputStream()) {
      return objectMapper.readValue(inputStream, typeReference);
    }
  }
}
