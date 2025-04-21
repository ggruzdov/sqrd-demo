package com.github.ggruzdov.sqrddemo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ggruzdov.sqrddemo.config.AppProperties;
import com.github.ggruzdov.sqrddemo.repository.OrderRepository;
import com.github.ggruzdov.sqrddemo.response.PlaceOrderResponse;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(properties = {
    "app.order-update-lifetime=2s"
})
@AutoConfigureMockMvc
@Transactional
class OrderControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AppProperties appProperties;

    @Test
    void placeOrderSuccessful() throws Exception {
        var placeOrderResult = mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "firstName": "Mike",
                        "lastName": "Johnson",
                        "phone": "5552223333",
                        "deliveryAddress": "5th Avenue, 13",
                        "pilotes": 5
                    }
                    """))
            .andExpect(status().isOk())
            .andReturn();

        var response = objectMapper.readValue(placeOrderResult.getResponse().getContentAsString(), PlaceOrderResponse.class);
        assertNotNull(response.id(), "Order id should be present after successful order placement");

        var result = orderRepository.findById(response.id()).orElseThrow();
        assertEquals("Mike", result.getCustomerFirstName());
        assertEquals("Johnson", result.getCustomerLastName());
        assertEquals("5552223333", result.getCustomerPhone());
        assertEquals("5th Avenue, 13", result.getDeliveryAddress());
        assertEquals(5, result.getPilotes());
        assertEquals(5 * appProperties.getPilotesPriceInCents(), result.getTotalPrice());
    }

    @Test
    void placeOrderWithInvalidData() throws Exception {
        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "firstName": "John",
                        "pilotes": 0
                    }
                    """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateOrderSuccessful() throws Exception {
        var placeOrderResult = mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "firstName": "Mike",
                        "lastName": "Johnson",
                        "phone": "5552223333",
                        "deliveryAddress": "5th Avenue, 13",
                        "pilotes": 15
                    }
                    """))
                .andExpect(status().isOk())
                .andReturn();

        var response = objectMapper.readValue(placeOrderResult.getResponse().getContentAsString(), PlaceOrderResponse.class);
        
        // Update the order
        mockMvc.perform(put("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("""
                    {
                        "id": %d,
                        "firstName": "Mike",
                        "lastName": "Johnson",
                        "phone": "5552223333",
                        "deliveryAddress": "5th Avenue, 13",
                        "pilotes": 10
                    }
                    """, response.id())))
                .andExpect(status().isOk());

        var result = orderRepository.findById(response.id())
            .orElseThrow(() -> new IllegalStateException("Order not found with id: " + response.id()));

        assertEquals("Mike", result.getCustomerFirstName());
        assertEquals("Johnson", result.getCustomerLastName());
        assertEquals("5552223333", result.getCustomerPhone());
        assertEquals("5th Avenue, 13", result.getDeliveryAddress());
        assertEquals(10, result.getPilotes());
        assertEquals(10 * appProperties.getPilotesPriceInCents(), result.getTotalPrice());
    }

    @Test
    void updateOrderFailsAfterAllowedTime() throws Exception {
        var placeOrderResult = mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "firstName": "Mike",
                        "lastName": "Johnson",
                        "phone": "5552223333",
                        "deliveryAddress": "5th Avenue, 13",
                        "pilotes": 15
                    }
                    """))
            .andExpect(status().isOk())
            .andReturn();

        var response = objectMapper.readValue(placeOrderResult.getResponse().getContentAsString(), PlaceOrderResponse.class);

        Awaitility.await()
            .pollDelay(appProperties.getOrderUpdateLifetime().plusSeconds(1))
            .until(() -> true);

        // Update the order
        mockMvc.perform(put("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("""
                    {
                        "id": %d,
                        "firstName": "Mike",
                        "lastName": "Johnson",
                        "phone": "5552223333",
                        "deliveryAddress": "5th Avenue, 13",
                        "pilotes": 10
                    }
                    """, response.id())))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateNonExistentOrder() throws Exception {
        mockMvc.perform(put("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "id": 100500,
                        "firstName": "Mike",
                        "lastName": "Johnson",
                        "phone": "5552223333",
                        "deliveryAddress": "5th Avenue, 13",
                        "pilotes": 10
                    }
                    """))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchOrdersWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/orders/search")
                .param("firstName", "Mike")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void placeThenSearchOrder() throws Exception {
        // First login to get authenticated session
        MockHttpSession session = new MockHttpSession();
        
        mockMvc.perform(post("/auth/login")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "phone": "5552223333",
                        "password": "mike1234"
                    }
                    """))
                .andExpect(status().isOk());
        
        // Place an order
        mockMvc.perform(post("/orders")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "firstName": "Mike",
                        "lastName": "Johnson",
                        "phone": "5552223333",
                        "deliveryAddress": "5th Avenue, 13",
                        "pilotes": 5
                    }
                    """))
                .andExpect(status().isOk());
        
        // Search for the order
        mockMvc.perform(get("/orders/search")
                .session(session)
                .param("firstName", "Mike")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].customerFirstName", is("Mike")));
    }
}