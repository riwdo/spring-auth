package com.projects.webapp.unitTests;

import com.projects.webapp.utils.JwtUtil;
import com.projects.webapp.models.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class JwtUtilTests {

    private static final JwtUtil jwtUtil = new JwtUtil("secret");
    private final UserEntity user = createMockUser("oscar", "test");

    private final UserDetails userDetails = createMockUserDetails("oscar", "test");
    private UserDetails createMockUserDetails(String username, String password) {
        return org.springframework.security.core.userdetails.User.builder().username(username).password(password).authorities("ROLE_USER").build();
    }

    private UserEntity createMockUser(String name, String password) {
        UserEntity user = new UserEntity();
        user.setName(name);
        user.setPassword(password);
        return user;
    }

    @Test
    void testTokenGenerationValidation() {
        String token = jwtUtil.generateToken(userDetails);
        assert(jwtUtil.validateToken(token, userDetails));
    }

    @Test
    void tokenGenerationNullUser_shouldThrowException() {
        String token = jwtUtil.generateToken(userDetails);
        assertThrows(NullPointerException.class, () -> jwtUtil.validateToken(token, null));
    }


}
