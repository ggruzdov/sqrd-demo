package com.github.ggruzdov.sqrddemo.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record LoginRequest(
    @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits")
    String phone,
    @NotNull
    String password
) {
}
