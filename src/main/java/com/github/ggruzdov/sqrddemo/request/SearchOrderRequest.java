package com.github.ggruzdov.sqrddemo.request;

public record SearchOrderRequest(
    String phone,
    String firstName,
    String lastName,
    Pagination pagination
) {
    public SearchOrderRequest {
        if (pagination == null) {
            pagination = Pagination.DEFAULT;
        }
    }
}
