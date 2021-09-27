package com.projects.webapp;

import io.jsonwebtoken.Jwts;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.UUID;

@RestController
public class AuthenticationController {

  private UserRepository userRepository;

  public AuthenticationController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @PostMapping(path = "/signUp")
  public Authentication signUp(@RequestBody LoginForm loginForm) {
    String username = loginForm.getUsername();
    String password = loginForm.getPassword();
    System.out.println("test");
    User user = userRepository.getByName(username);

    if (user != null) {
      return new Authentication("User already exists");
    }

    User newUser = new User();

    newUser.setName(username);
    newUser.setPassword(password);
    userRepository.save(newUser);

    return new Authentication("Created user!");
  }

  @PostMapping(path = "/signIn")
  public ResponseEntity<?> signIn(@RequestBody LoginForm loginForm) {
    String username = loginForm.getUsername();
    String password = loginForm.getPassword();

    String jwt = Jwts.builder().setId(UUID.randomUUID().toString()).setExpiration(new Date(System.currentTimeMillis() + 3600000)).compact();
    User user = userRepository.getByName(username);

    if (user == null) {
      return ResponseEntity.ok("User does not exist");
    }

    if (user.getPassword().equals(password)) {
      return ResponseEntity.ok(new JwtResponse(jwt));
    }
    return ResponseEntity.ok("Password incorrect");
  }


  /*@GetMapping("/testAuth")
  public ResponseEntity<?> testAuth() {

  }*/

  
}
