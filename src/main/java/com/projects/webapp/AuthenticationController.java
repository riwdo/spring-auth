package com.projects.webapp;

import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.UUID;

@RestController
public class AuthenticationController {

  private UserRepository userRepository;

  Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

  @Autowired
  public AuthenticationController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @PostMapping(path = "/signUp")
  public ResponseEntity<Authentication> signUp(@RequestBody LoginForm loginForm) {
    logger.info("Request to create user: {}", loginForm);

    String username = loginForm.getUsername();
    String password = loginForm.getPassword();

    User user = userRepository.getByName(username);

    if (user != null) {
      logger.info("User already exists!");
      return new ResponseEntity<>(new Authentication("User already exists"), HttpStatus.CONFLICT);
    }

    User newUser = new User();

    newUser.setName(username);
    newUser.setPassword(password);
    userRepository.save(newUser);
    logger.info("User created!");

    return new ResponseEntity<>(new Authentication("Created user!"), HttpStatus.CREATED);
  }

  @PostMapping(path = "/signIn")
  public ResponseEntity<Authentication> signIn(@RequestBody LoginForm loginForm) {
    logger.info("Request to login user: {}", loginForm);

    String username = loginForm.getUsername();
    String password = loginForm.getPassword();

    String jwt = Jwts.builder().setId(UUID.randomUUID().toString()).setExpiration(new Date(System.currentTimeMillis() + 3600000)).compact();
    User user = userRepository.getByName(username);

    if (user == null) {
      logger.info("User does not exist!");
      return new ResponseEntity<>(new Authentication("User does not exist"), HttpStatus.NOT_FOUND);
    }

    if (user.getPassword().equals(password)) {
      logger.info("User logged in!");
      return new ResponseEntity<>(new Authentication(jwt), HttpStatus.OK);
    }

    logger.info("Provided password is incorrect");
    return new ResponseEntity<>(new Authentication("Password incorrect"), HttpStatus.UNAUTHORIZED);
  }

  
}
