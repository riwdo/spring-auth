package com.projects.webapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;;
import static org.mockito.Mockito.*;

@WebMvcTest(controllers = AuthenticationController.class)
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    @Test
    void whenNewUserRegistered_thenReturns201() throws Exception {
        LoginForm loginForm = new LoginForm();
        loginForm.setUsername("oscar");
        loginForm.setPassword("test");

        this.mockMvc.perform(post("/signUp")
                .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginForm)))
                .andExpect(status().isCreated());
    }

    @Test
    void whenExistingUserRegister_thenReturns409() throws Exception {
        LoginForm loginForm = new LoginForm();
        loginForm.setUsername("oscar");
        loginForm.setPassword("test");

        User user = new User();
        user.setName("oscar");
        user.setPassword("test");

        when(userRepository.getByName("oscar")).thenReturn(user);
        this.mockMvc.perform(post("/signUp")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginForm)))
                .andExpect(status().isConflict());
    }


    @Test
    void whenUserNotExistLogin_thenReturns404() throws Exception {
        LoginForm loginForm = new LoginForm();
        loginForm.setUsername("oscar");
        loginForm.setPassword("test");

        when(userRepository.getByName("oscar")).thenReturn(null);
        this.mockMvc.perform(post("/signIn")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(loginForm)))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenUserExistLogin_thenReturns200() throws Exception {
        LoginForm loginForm = new LoginForm();
        loginForm.setUsername("oscar");
        loginForm.setPassword("test");

        User user = new User();
        user.setName("oscar");
        user.setPassword("test");

        when(userRepository.getByName("oscar")).thenReturn(user);
        this.mockMvc.perform(post("/signIn")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginForm)))
                .andExpect(status().isOk());
    }

    @Test
    void whenUserExistLoginWrongPassword_thenReturns401() throws Exception {
        LoginForm loginForm = new LoginForm();
        loginForm.setUsername("oscar");
        loginForm.setPassword("test");

        User user = new User();
        user.setName("oscar");
        user.setPassword("testar");

        when(userRepository.getByName("oscar")).thenReturn(user);
        this.mockMvc.perform(post("/signIn")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginForm)))
                .andExpect(status().isUnauthorized());
    }


}
