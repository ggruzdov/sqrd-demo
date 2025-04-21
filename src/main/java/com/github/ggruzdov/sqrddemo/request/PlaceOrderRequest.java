package com.github.ggruzdov.sqrddemo.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PlaceOrderRequest(
    @NotBlank
    @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits")
    String phone,
    @NotBlank
    String firstName,
    @NotBlank
    String lastName,
    @NotBlank
    String deliveryAddress,
    @NotBlank
    @Pattern(regexp = "^(5|10|15)$", message = "Pilotes quantity must be either 5, 10, or 15")
    String pilotes
) {
}
