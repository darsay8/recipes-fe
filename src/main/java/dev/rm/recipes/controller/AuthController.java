package dev.rm.recipes.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import dev.rm.recipes.model.User;
import dev.rm.recipes.service.UserService;
import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

  private UserService userService;

  public AuthController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/login")
  public String login() {
    return "login";
  }

  @PostMapping("/login")
  public String login(@ModelAttribute User user, HttpSession session, Model model) {
    Map<String, Object> authData = userService.authenticate(user);

    if (authData != null && authData.containsKey("token")) {
      String token = (String) authData.get("token");
      String username = (String) authData.get("username");
      String email = (String) authData.get("email");
      String role = (String) authData.get("role");

      session.setAttribute("token", token);
      session.setAttribute("username", username);
      session.setAttribute("email", email);
      session.setAttribute("role", role);
      return "redirect:/recipes";
    } else {
      model.addAttribute("error", "Invalid credentials");
      return "login";
    }
  }

  @GetMapping("/logout")
  public String logout(HttpSession session) {
    session.invalidate();
    return "redirect:/recipes";
  }

  @GetMapping("/register")
  public String register() {
    return "register";
  }

  @PostMapping("/register")
  public String register(@ModelAttribute User user, HttpSession session, Model model) {

    if (!user.getPassword().equals(user.getConfirmPassword())) {
      model.addAttribute("error", "Passwords do not match.");
      return "register";
    }

    Map<String, Object> authData = userService.register(user);

    if (authData != null && authData.containsKey("token")) {

      String token = (String) authData.get("token");
      String username = (String) authData.get("username");
      String email = (String) authData.get("email");
      String role = (String) authData.get("role");

      session.setAttribute("token", token);
      session.setAttribute("username", username);
      session.setAttribute("email", email);
      session.setAttribute("role", role);
      return "redirect:/recipes";
    } else {
      model.addAttribute("error", "Registration failed. Please try again.");
      return "register";
    }

  }

}
