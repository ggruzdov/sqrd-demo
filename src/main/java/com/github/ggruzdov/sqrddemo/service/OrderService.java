package com.github.ggruzdov.sqrddemo.service;

import com.github.ggruzdov.sqrddemo.config.AppProperties;
import com.github.ggruzdov.sqrddemo.model.Order;
import com.github.ggruzdov.sqrddemo.repository.OrderRepository;
import com.github.ggruzdov.sqrddemo.request.PlaceOrderRequest;
import com.github.ggruzdov.sqrddemo.request.SearchOrderRequest;
import com.github.ggruzdov.sqrddemo.request.UpdateOrderRequest;
import com.github.ggruzdov.sqrddemo.response.SearchOrderResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.hibernate.StaleObjectStateException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedModel;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;


@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final AppProperties appProperties;

    public Order placeOrder(PlaceOrderRequest request) {
        var pilotes = Integer.parseInt(request.pilotes());
        var order = Order.builder()
            .version(1)
            .customerFirstName(request.firstName())
            .customerLastName(request.lastName())
            .customerPhone(request.phone())
            .deliveryAddress(request.deliveryAddress())
            .pilotes(pilotes)
            .totalPrice(pilotes * appProperties.getPilotesPriceInCents())
            .build();

        return orderRepository.save(order);
    }

    @Transactional
    public void updateOrder(UpdateOrderRequest request) {
        var order = orderRepository.findById(request.id())
            .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + request.id()));

        if (order.getCreatedAt().plus(appProperties.getOrderUpdateLifetime()).isBefore(Instant.now())) {
            throw new IllegalStateException(
                "Order update lifetime expired. You can change the order only within " +
                    appProperties.getOrderUpdateLifetime().toMinutes() + " minutes after its creation."
            );
        }

        var pilotes = Integer.parseInt(request.pilotes());
        order.setCustomerFirstName(request.firstName());
        order.setCustomerLastName(request.lastName());
        order.setCustomerPhone(request.phone());
        order.setDeliveryAddress(request.deliveryAddress());
        order.setPilotes(pilotes);
        order.setTotalPrice(pilotes * appProperties.getPilotesPriceInCents());

        try {
            orderRepository.saveAndFlush(order);
        } catch (ObjectOptimisticLockingFailureException | StaleObjectStateException e) {
            throw new OptimisticLockException("Order was modified by another transaction. Please refresh and try again.", e);
        }
    }

    public PagedModel<SearchOrderResponse> searchOrders(SearchOrderRequest request) {
        Specification<Order> spec = Specification.where(null);

        if (StringUtils.hasText(request.phone())) {
            spec = spec.and((root, query, cb) ->
                cb.equal(root.get("customerPhone"), request.phone()));
        }

        if (StringUtils.hasText(request.firstName())) {
            spec = spec.and((root, query, cb) ->
                cb.like(root.get("customerFirstName"), "%" + request.firstName() + "%"));
        }

        if (StringUtils.hasText(request.lastName())) {
            spec = spec.and((root, query, cb) ->
                cb.like(root.get("customerLastName"), "%" + request.lastName() + "%"));
        }

        var pageRequest = PageRequest.of(request.pagination().page() - 1, request.pagination().limit());
        var result = orderRepository
            .findAll(spec, pageRequest)
            .map(SearchOrderResponse::from);

        return new PagedModel<>(result);
    }
}
