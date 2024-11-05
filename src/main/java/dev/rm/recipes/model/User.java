package dev.rm.recipes.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
  private String username;
  private String password; // Consider not exposing this in your API responses
  private String roles; // e.g., "USER", "ADMIN"
}