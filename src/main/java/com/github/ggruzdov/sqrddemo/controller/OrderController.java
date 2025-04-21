package com.github.ggruzdov.sqrddemo.controller;

import com.github.ggruzdov.sqrddemo.request.PlaceOrderRequest;
import com.github.ggruzdov.sqrddemo.request.SearchOrderRequest;
import com.github.ggruzdov.sqrddemo.request.UpdateOrderRequest;
import com.github.ggruzdov.sqrddemo.response.PlaceOrderResponse;
import com.github.ggruzdov.sqrddemo.response.SearchOrderResponse;
import com.github.ggruzdov.sqrddemo.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
@Tag(name = "Order management series", description = "API endpoints for managing orders")
public class OrderController {

    private final OrderService orderService;

    @Operation(
        summary = "Place a new order",
        description = "Creates a new order with the specified number of pilotes and customer information"
    )
    @PostMapping
    public PlaceOrderResponse placeOrder(@RequestBody @Valid PlaceOrderRequest request) {
        log.info("Placing order from {}, for {} pilotes", request.firstName(), request.pilotes());
        var order = orderService.placeOrder(request);

        return new PlaceOrderResponse(order.getId());
    }

    @Operation(
        summary = "Update existing order",
        description = "Modifies an existing order within the allowed time window"
    )
    @PutMapping
    public void updateOrder( @RequestBody @Valid UpdateOrderRequest request) {
        log.info("Updating {} order", request.id());
        orderService.updateOrder(request);
    }

    @Operation(
        summary = "Search orders",
        description = "Retrieves a paginated list of orders based on search criteria"
    )
    @GetMapping("/search")
    public PagedModel<SearchOrderResponse> searchOrders(@Valid SearchOrderRequest request) {
        log.info("Searching orders by filter {}", request);
        return orderService.searchOrders(request);
    }
}
