package com.projects.webapp.controllers;

import com.projects.webapp.*;
import com.projects.webapp.models.JwtResponse;
import com.projects.webapp.models.LoginForm;
import com.projects.webapp.models.UserEntity;
import com.projects.webapp.services.UserDetailsServiceImpl;
import com.projects.webapp.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class AuthenticationController {

  private PasswordEncoder bCryptPasswordEncoder;

  private AuthenticationManager authenticationManager;

  private UserDetailsServiceImpl userDetailsService;

  private UserRepository userRepository;

  private JwtUtil jwtUtil;

  Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

  @Autowired
  public AuthenticationController(PasswordEncoder bCryptPasswordEncoder, AuthenticationManager authenticationManager, UserDetailsServiceImpl userDetailsService, UserRepository userRepository, JwtUtil jwtutil) {
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    this.authenticationManager = authenticationManager;
    this.userDetailsService = userDetailsService;
    this.userRepository = userRepository;
    this.jwtUtil = jwtutil;
  }

  @PostMapping(path = "/signUp")
  public ResponseEntity<String> signUp(@RequestBody LoginForm loginForm) {
    logger.info("Request to create user: {}", loginForm);

    String username = loginForm.getUsername();
    String password = loginForm.getPassword();

    UserEntity user = userRepository.getByName(username);

    if (user != null) {
      logger.info("User already exists!");
      throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
    }

    UserEntity newUser = new UserEntity();
    newUser.setName(username);
    newUser.setPassword(bCryptPasswordEncoder.encode(password));

    userRepository.save(newUser);
    logger.info("User created!");

    return new ResponseEntity<>("Created user!", HttpStatus.CREATED);
  }

  @PostMapping(path = "/signIn")
  public ResponseEntity<JwtResponse> signIn(@RequestBody LoginForm loginForm) {
    logger.info("Request to login user: {}", loginForm);

    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginForm.getUsername(), loginForm.getPassword()));

    UserDetails userDetails = userDetailsService.loadUserByUsername(loginForm.getUsername());

    String jwt = jwtUtil.generateToken(userDetails);

    return ResponseEntity.ok(new JwtResponse(jwt));
  }

  @PostMapping(path = "/authenticationTest")
  public ResponseEntity<String> authenticationTest(@RequestHeader("Authorization") String authHeader) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated()) {
      return new ResponseEntity<>("Authorized!", HttpStatus.OK);
    }
    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized!");

  }

  
}
