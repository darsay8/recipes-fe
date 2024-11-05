package dev.rm.recipes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import dev.rm.recipes.model.Recipe;
import dev.rm.recipes.service.RecipeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Controller
public class RecipeController {

  @Autowired
  private RecipeService recipeService;

  @GetMapping("/")
  public String getRecipes(Model model, HttpSession session) {
    List<Recipe> recipes = recipeService.fetchAllRecipes();
    model.addAttribute("recipes", recipes);
    return "recipes";
  }

  @GetMapping("/recipes")
  public String getAllRecipes(Model model, HttpSession session) {
    List<Recipe> recipes = recipeService.fetchAllRecipes();
    model.addAttribute("recipes", recipes);
    return "recipes";
  }

  @GetMapping("/recipes/{id}")
  public String getRecipeDetail(@PathVariable Long id, Model model, HttpSession session) {
    String token = (String) session.getAttribute("token");
    if (token == null) {
      return "redirect:/login";
    }

    Recipe recipe = recipeService.getRecipeById(id, token);
    if (recipe == null) {
      return "error";
    }

    model.addAttribute("recipe", recipe);
    return "recipe-detail";
  }

}