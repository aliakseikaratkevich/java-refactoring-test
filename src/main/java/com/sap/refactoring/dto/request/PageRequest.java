package com.sap.refactoring.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public record PageRequest(
    @Min(value = 0, message = "Page number must be non-negative")
    int page,

    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size cannot exceed 100")
    int size,

    @NotBlank(message = "Sort field cannot be blank")
    @Pattern(regexp = "^(id|email|name)$", message = "Sort field must be one of: id, email, name")
    String sortBy,

    @NotBlank(message = "Sort direction cannot be blank")
    @Pattern(regexp = "^(ASC|DESC)$", message = "Sort direction must be either ASC or DESC")
    String sortDirection
) {

  public Pageable toPageable() {
    Sort sort = "DESC".equalsIgnoreCase(sortDirection)
        ? Sort.by(sortBy).descending()
        : Sort.by(sortBy).ascending();

    return org.springframework.data.domain.PageRequest.of(page, size, sort);
  }
}
