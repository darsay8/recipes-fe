package dev.rm.recipes.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dev.rm.recipes.model.User;
import dev.rm.recipes.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Controller
@RequestMapping("/")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @RequestMapping("admin/users")
  public String getUsersToAdmin(Model model, HttpSession session) {

    String token = (String) session.getAttribute("token");
    if (token == null) {
      return "redirect:/login";
    }

    List<User> users = userService.getUsers(token);

    model.addAttribute("users", users);

    return "admin-users";
  }

  @GetMapping("/admin/users/{id}")
  public String showEditUserForm(@PathVariable Long id, Model model, HttpSession session) {

    String token = (String) session.getAttribute("token");
    if (token == null) {
      return "redirect:/login";
    }

    User user = userService.getUserById(token, id);
    model.addAttribute("user", user);
    return "admin-users";
  }

  // @PostMapping("/admin/users")
  // public String createUser(@RequestParam String username,
  // @RequestParam String email,
  // @RequestParam String password,
  // @RequestParam String role,
  // RedirectAttributes redirectAttributes, HttpSession session) {

  // String token = (String) session.getAttribute("token");
  // if (token == null) {
  // return "redirect:/login";
  // }

  // User newUser = new User();
  // newUser.setUsername(username);
  // newUser.setEmail(email);
  // newUser.setPassword(password);
  // newUser.setRole(role);

  // boolean isCreated = userService.createUser(token, newUser);

  // if (isCreated) {
  // redirectAttributes.addFlashAttribute("message", "User successfully
  // created.");
  // } else {
  // redirectAttributes.addFlashAttribute("message", "Failed to create user.
  // Please try again.");
  // }

  // return "redirect:/admin/users";
  // }

  // @PutMapping("admin/users/{id}")
  // public String updateUser(
  // @PathVariable Long id,
  // @RequestParam String username,
  // @RequestParam String email,
  // @RequestParam String password,
  // @RequestParam String role,
  // RedirectAttributes redirectAttributes,
  // HttpSession session) {

  // String token = (String) session.getAttribute("token");
  // if (token == null) {
  // return "redirect:/login";
  // }

  // User updatedUser = new User();
  // updatedUser.setUsername(username);
  // updatedUser.setEmail(email);

  // if (password == null || password.isEmpty()) {
  // User user = userService.getUserById(token, id);
  // updatedUser.setPassword(user.getPassword());
  // } else {
  // updatedUser.setPassword(password);
  // }
  // updatedUser.setRole(role);

  // boolean isUpdated = userService.updateUser(token, updatedUser);

  // if (isUpdated) {
  // redirectAttributes.addFlashAttribute("message", "User successfully
  // updated.");
  // } else {
  // redirectAttributes.addFlashAttribute("message", "Failed to update user.
  // Please try again.");
  // }

  // return "redirect:/admin/users";
  // }

  @PostMapping("/admin/users")
  public String createOrUpdateUser(
      @RequestParam(required = false) Long userId,
      @RequestParam String username,
      @RequestParam String email,
      @RequestParam(required = false) String password,
      @RequestParam String role,
      RedirectAttributes redirectAttributes,
      HttpSession session) {

    String token = (String) session.getAttribute("token");
    if (token == null) {
      return "redirect:/login";
    }

    boolean isNewUser = (userId == null);
    User user = isNewUser ? new User() : userService.getUserById(token, userId);

    user.setUsername(username);
    user.setEmail(email);

    if (password != null && !password.isEmpty()) {
      user.setPassword(password);
    } else if (!isNewUser) {
      User existingUser = userService.getUserById(token, userId);
      user.setPassword(existingUser.getPassword());
    }

    user.setRole(role);

    boolean isSuccess;
    if (isNewUser) {
      // Create new user
      isSuccess = userService.createUser(token, user);
    } else {
      isSuccess = userService.updateUser(token, user);
    }

    if (isSuccess) {
      redirectAttributes.addFlashAttribute("message",
          isNewUser ? "User successfully created." : "User successfully updated.");
    } else {
      redirectAttributes.addFlashAttribute("message",
          isNewUser ? "Failed to create user. Please try again." : "Failed to update user. Please try again.");
    }

    return "redirect:/admin/users";
  }

  @DeleteMapping("admin/users/{id}")
  public String deleteUser(@PathVariable Long id, Model model, HttpSession session) {

    String token = (String) session.getAttribute("token");

    if (token == null) {
      return "redirect:/login";
    }

    boolean isDeleted = userService.deleteUser(token, id);

    if (isDeleted) {
      model.addAttribute("message", "User successfully deleted.");
    } else {
      model.addAttribute("message", "Failed to delete user. Please try again.");
    }

    List<User> users = userService.getUsers(token);
    model.addAttribute("users", users);

    return "redirect:/admin/users";
  }

}
