package com.projects.webapp;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootTest
class WebappApplicationTests {

  @Test
  void contextLoads() {
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
