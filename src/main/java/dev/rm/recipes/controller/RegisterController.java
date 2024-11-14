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
public class RegisterController {

  @Autowired
  private UserService userService;

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

    String token = userService.register(user);

    if (token != null) {
      session.setAttribute("token", token);
      return "redirect:/recipes";
    } else {
      model.addAttribute("error", "Registration failed. Please try again.");
      return "register";
    }

  }
}
