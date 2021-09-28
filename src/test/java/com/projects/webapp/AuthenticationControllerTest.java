package com.projects.webapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;

@WebMvcTest(controllers = AuthenticationController.class)
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserRepository userRepository;

    private User createMockUser(String name, String password) {
        User user = new User();
        user.setName(name);
        user.setPassword(password);
        return user;
    }

    private LoginForm createMockLoginForm(String name, String password) {
        LoginForm loginForm = new LoginForm();
        loginForm.setUsername(name);
        loginForm.setPassword(password);
        return loginForm;
    }

    @Test
    void whenNewUserRegistered_thenReturns201() throws Exception {
        LoginForm loginForm = createMockLoginForm("oscar", "test");

        this.mockMvc.perform(post("/signUp")
                .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginForm)))
                .andExpect(status().isCreated());
    }

    @Test
    void whenExistingUserRegister_thenReturns409() throws Exception {
        LoginForm loginForm = createMockLoginForm("oscar", "test");
        User user = createMockUser("oscar", "test");

        when(userRepository.getByName(loginForm.getUsername())).thenReturn(user);
        this.mockMvc.perform(post("/signUp")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginForm)))
                .andExpect(status().isConflict());
    }


    @Test
    void whenUserNotExistLogin_thenReturns404() throws Exception {
        LoginForm loginForm = createMockLoginForm("oscar", "test");

        when(userRepository.getByName(loginForm.getUsername())).thenReturn(null);
        this.mockMvc.perform(post("/signIn")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(loginForm)))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenUserExistLogin_thenReturns200() throws Exception {
        LoginForm loginForm = createMockLoginForm("oscar", "test");
        User user = createMockUser("oscar", "test");

        when(userRepository.getByName(loginForm.getUsername())).thenReturn(user);
        this.mockMvc.perform(post("/signIn")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginForm)))
                .andExpect(status().isOk());
    }

    @Test
    void whenUserExistLoginWrongPassword_thenReturns401() throws Exception {
        LoginForm loginForm = createMockLoginForm("oscar", "test");
        User user = createMockUser("oscar", "testar");

        when(userRepository.getByName(loginForm.getUsername())).thenReturn(user);
        this.mockMvc.perform(post("/signIn")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginForm)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenUserLoggedInAccessAuthPath_thenReturns200() throws Exception {
        User user = createMockUser("oscar", "test");

        String jwt = "test";

        when(jwtUtil.getUsernameFromToken("test")).thenReturn("oscar");
        when(jwtUtil.validateToken("test", user)).thenReturn(true);
        when(userRepository.getByName(user.getName())).thenReturn(user);

        this.mockMvc.perform(post("/authenticationTest")
                .header("Authorization","Bearer " + jwt))
                .andExpect(status().isOk());
    }

    @Test
    void whenUserNotLoggedInAccessAuthPath_thenReturns401() throws Exception {
        String invalidToken = "token";

        this.mockMvc.perform(post("/authenticationTest")
                        .header("Authorization",invalidToken))
                .andExpect(status().isUnauthorized());
    }

}
