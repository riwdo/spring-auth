package com.projects.webapp;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class WebappApplicationTests {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserRepository userRepository;

  @Test
  void contextLoads() {
  }

  @Test
  void test() {

    User testUser = new User();
    Mockito.when(userRepository.getByName("Oscar")).thenReturn(testUser);
    assert(1 == 1);
  }


  // Added to mock database layer during test. No MySQL initialization required.
  @Configuration
  static class TestConfig {
    @Bean
    UserRepository userRepository() {
      return Mockito.mock(UserRepository.class);
    }
  }



}
