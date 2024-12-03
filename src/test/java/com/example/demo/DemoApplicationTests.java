package com.example.demo;

import org.junit.jupiter.api.Test;

import com.example.demo.service.LoginService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LoginServiceTest {

    private final LoginService loginService = new LoginService();

    @Test
    void testGetEmailFromToken_ValidToken() {
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InRlc3RAZXhhbXBsZS5jb20ifQ.sB1zKUv8Z3LMPuQu3lpS3gXLzTQfA8gFvQJ1pZWq8XA";

        String email = loginService.getEmailFromToken(token);

        assertEquals("test@example.com", email, "Email extracted from token is incorrect");
    }

    @Test
    void testGetEmailFromToken_InvalidToken() {
        String token = "invalid.token.value";

        String email = loginService.getEmailFromToken(token);

        assertNull(email, "Email should be null for an invalid token");
    }
}
