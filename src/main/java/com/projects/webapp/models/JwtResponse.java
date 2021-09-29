package com.projects.webapp.models;

public class JwtResponse {

  private String jwt;

  public JwtResponse(String jwt) {
    this.jwt = jwt;
  }

  public String getJwt() {
    return this.jwt;
  }

  public void setJwt(String jwt) {
    this.jwt = jwt;
  }

}
