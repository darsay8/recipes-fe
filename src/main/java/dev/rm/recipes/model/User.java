package dev.rm.recipes.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
  private Long userId;
  private String username;
  private String email;
  private String password;
  private String confirmPassword;
  private String role;
}