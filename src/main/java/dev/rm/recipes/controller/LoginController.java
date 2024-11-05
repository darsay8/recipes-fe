package dev.rm.recipes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import dev.rm.recipes.model.User;
import dev.rm.recipes.service.UserService;
import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

  @Autowired
  private UserService userService;

  @GetMapping("/login")
  public String login() {
    return "login";
  }

  @PostMapping("/login")
  public String login(@ModelAttribute User user, HttpSession session, Model model) {
    String token = userService.authenticate(user);

    if (token != null) {
      session.setAttribute("token", token);
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
}
