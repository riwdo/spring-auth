package com.projects.webapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class AuthenticationController {

  private UserRepository userRepository;

  private JwtUtil jwtUtil;

  Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

  @Autowired
  public AuthenticationController(UserRepository userRepository, JwtUtil jwtutil) {
    this.userRepository = userRepository;
    this.jwtUtil = jwtutil;
  }

  @PostMapping(path = "/signUp")
  public ResponseEntity<String> signUp(@RequestBody LoginForm loginForm) {
    logger.info("Request to create user: {}", loginForm);

    String username = loginForm.getUsername();
    String password = loginForm.getPassword();

    User user = userRepository.getByName(username);

    if (user != null) {
      logger.info("User already exists!");
      throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
    }

    User newUser = new User();

    newUser.setName(username);
    newUser.setPassword(password);
    userRepository.save(newUser);
    logger.info("User created!");

    return new ResponseEntity<>("Created user!", HttpStatus.CREATED);
  }

  @PostMapping(path = "/signIn")
  public ResponseEntity<JwtResponse> signIn(@RequestBody LoginForm loginForm) {
    logger.info("Request to login user: {}", loginForm);

    String username = loginForm.getUsername();
    String password = loginForm.getPassword();

    User user = userRepository.getByName(username);
    String jwt = jwtUtil.generateToken(user);

    if (user == null) {
      logger.info("User does not exist!");
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist");
    }

    if (user.getPassword().equals(password)) {
      logger.info("User logged in!");
      return new ResponseEntity<>(new JwtResponse(jwt), HttpStatus.OK);
    }

    logger.info("Provided password is incorrect");
    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Password incorrect");
  }

  @PostMapping(path = "/authenticationTest")
  public ResponseEntity<String> authenticationTest(@RequestHeader("Authorization") String authHeader) {
    try {
      String[] authParams = authHeader.split(" ");
      // Check valid header bearer
      if (authParams[0] != null && authParams[0].equals("Bearer")) {
        String token = authParams[1];
        String username = jwtUtil.getUsernameFromToken(token);
        User user = userRepository.getByName(username);
        if (jwtUtil.validateToken(token, user)) {
          return new ResponseEntity<>("Authorized!", HttpStatus.OK);
        }
      }
    } catch (Exception exo) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized!");
    }
    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized!");
  }

  
}
