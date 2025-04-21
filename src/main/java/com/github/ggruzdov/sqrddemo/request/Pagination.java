package com.github.ggruzdov.sqrddemo.request;

import jakarta.validation.constraints.NotNull;

public record Pagination(
    @NotNull
    Integer page,
    @NotNull
    Integer limit
) {
    public static Pagination DEFAULT = new Pagination(1, 20);
}