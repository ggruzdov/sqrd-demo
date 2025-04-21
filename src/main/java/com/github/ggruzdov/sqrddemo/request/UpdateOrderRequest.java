package com.github.ggruzdov.sqrddemo.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UpdateOrderRequest(
    @NotNull
    Integer id,
    @NotNull
    @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits")
    String phone,
    @NotNull
    String firstName,
    @NotNull
    String lastName,
    @NotNull
    String deliveryAddress,
    @NotNull
    @Pattern(regexp = "^(5|10|15)$", message = "Pilotes quantity must be either 5, 10, or 15")
    String pilotes
) {
}
