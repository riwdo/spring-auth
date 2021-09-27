package com.projects.webapp;

public class JwtResponse {

  private String jwt;

  JwtResponse(String jwt) {
    this.jwt = jwt;
  }

  public String getJwt() {
    return this.jwt;
  }

  public void setJwt(String jwt) {
    this.jwt = jwt;
  }

}
