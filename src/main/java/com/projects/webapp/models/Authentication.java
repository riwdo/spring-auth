package com.projects.webapp.models;

public class Authentication {
  private String text;

  Authentication(String text) {
    this.text = text;
  }

  public String getText() {
    return this.text;
  }

  public void setText(String text) {
    this.text = text;
  }
}
