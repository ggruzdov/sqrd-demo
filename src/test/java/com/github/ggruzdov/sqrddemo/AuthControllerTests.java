package com.github.ggruzdov.sqrddemo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SecurityContextRepository securityContextRepository;

    @Test
    void loginFailsWithInvalidCredentials() throws Exception {
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "phone": "1234567890",
                        "password": "wrongpassword"
                    }
                    """))
            .andExpect(status().isBadRequest());
    }

    @Test
    void loginSuccessful() throws Exception {
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                                            "phone": "5552223333",
                                            "password": "mike1234"
                    }
                    """))
            .andExpect(status().isOk());
    }

    @Test
    void loginCreatesSessionAndLogoutClearsSession() throws Exception {
        MockHttpSession session = new MockHttpSession();

        // Perform login and keep the session
        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "phone": "5552223333",
                        "password": "mike1234"
                    }
                    """))
                .andExpect(status().isOk())
                .andReturn();

        // Get the session after login
        MockHttpSession sessionAfterLogin = (MockHttpSession) loginResult.getRequest().getSession();
        assertNotNull(sessionAfterLogin, "Session should be created after login");

        // Create a mock request to check if authentication is stored in the session
        var mockRequest = new MockHttpServletRequest();
        mockRequest.setSession(sessionAfterLogin);

        // Authentication should be present in the security context
        var contextAfterLogin = securityContextRepository.loadDeferredContext(mockRequest);
        assertNotNull(contextAfterLogin.get().getAuthentication(),
                "Authentication should be present in the security context after login");

        // Perform logout with the same session
        mockMvc.perform(post("/auth/logout")
                .session(sessionAfterLogin)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        var contextAfterLogout = securityContextRepository.loadDeferredContext(mockRequest);
        assertNull(contextAfterLogout.get().getAuthentication(), "Authentication should be cleared after logout");
    }
}