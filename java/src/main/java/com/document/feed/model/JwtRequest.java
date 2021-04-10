package com.document.feed.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtRequest implements Serializable {

  private static final long serialVersionUID = 5926468583005150707L;

  private String username;

  private String password;

  // need default constructor for JSON Parsing
  public JwtRequest() {}
}
