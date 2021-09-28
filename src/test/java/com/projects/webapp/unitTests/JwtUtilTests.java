package com.projects.webapp.unitTests;

import com.projects.webapp.JwtUtil;
import com.projects.webapp.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class JwtUtilTests {

    private static final JwtUtil jwtUtil = new JwtUtil("secret");
    private final User user = createMockUser("oscar", "test");

    private User createMockUser(String name, String password) {
        User user = new User();
        user.setName(name);
        user.setPassword(password);
        return user;
    }

    @Test
    void testTokenGenerationValidation() {
        String token = jwtUtil.generateToken(user);
        assert(jwtUtil.validateToken(token, user));
    }

    @Test
    void tokenGenerationNullUser_shouldThrowException() {
        String token = jwtUtil.generateToken(user);
        assertThrows(NullPointerException.class, () -> jwtUtil.validateToken(token, null));
    }


}
