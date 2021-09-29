package com.projects.webapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projects.webapp.controllers.AuthenticationController;
import com.projects.webapp.models.LoginForm;
import com.projects.webapp.models.UserEntity;
import com.projects.webapp.services.UserDetailsServiceImpl;
import com.projects.webapp.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
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

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private PasswordEncoder bCryptPasswordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    private UserDetails createMockUserDetails(String username, String password) {
        return org.springframework.security.core.userdetails.User.builder().username(username).password(password).authorities("ROLE_USER").build();
    }

    private UserEntity createMockUser(String name, String password) {
        UserEntity user = new UserEntity();
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
        UserEntity user = createMockUser("oscar", "test");

        when(userRepository.getByName(loginForm.getUsername())).thenReturn(user);
        this.mockMvc.perform(post("/signUp")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginForm)))
                .andExpect(status().isConflict());
    }


    @Test
    void whenUserNotExistLogin_thenReturns403() throws Exception {
        LoginForm loginForm = createMockLoginForm("oscar", "test");

        when(userRepository.getByName(loginForm.getUsername())).thenReturn(null);

        this.mockMvc.perform(post("/signIn")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(loginForm)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "oscar", password = "test")
    void whenUserExistLogin_thenReturns200() throws Exception {
        LoginForm loginForm = createMockLoginForm("oscar", "test");

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        UserDetails userDetails = createMockUserDetails("oscar", bCryptPasswordEncoder.encode("test"));
        when(userDetailsService.loadUserByUsername(loginForm.getUsername())).thenReturn(userDetails);

        this.mockMvc.perform(post("/signIn")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginForm)))
                .andExpect(status().isOk());
    }

    @Test
    void whenUserExistLoginWrongPassword_thenReturns403() throws Exception {
        LoginForm loginForm = createMockLoginForm("oscar", "test");
        UserEntity user = createMockUser("oscar", "testar");

        when(userRepository.getByName(loginForm.getUsername())).thenReturn(user);
        this.mockMvc.perform(post("/signIn")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginForm)))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenUserLoggedInAccessAuthPath_thenReturns200() throws Exception {
        UserDetails userDetails = createMockUserDetails("oscar", "test");
        UserEntity user = createMockUser("oscar", "test");
        String jwt = "test";

        when(jwtUtil.getUsernameFromToken("test")).thenReturn("oscar");
        when(jwtUtil.validateToken("test", userDetails)).thenReturn(true);
        when(userRepository.getByName(userDetails.getUsername())).thenReturn(user);
        when(userDetailsService.loadUserByUsername(user.getName())).thenReturn(userDetails);

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
