package dev.rm.recipes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import dev.rm.recipes.model.Difficulty;
import dev.rm.recipes.model.MealType;
import dev.rm.recipes.model.Recipe;
import dev.rm.recipes.service.RecipeService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

@Slf4j
@Controller
public class RecipeController {

  @Autowired
  private RecipeService recipeService;

  @GetMapping("/")
  public String getRecipes(Model model, HttpSession session) {
    List<Recipe> recipes = recipeService.fetchAllRecipes();
    populateModelWithRecipesAndCountries(model, recipes);
    return "recipes";
  }

  @GetMapping("/recipes")
  public String getAllRecipes(Model model, HttpSession session) {
    List<Recipe> recipes = recipeService.fetchAllRecipes();
    populateModelWithRecipesAndCountries(model, recipes);
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

  @GetMapping("/recipes/search")
  public String searchRecipes(
      @RequestParam(value = "name", required = false) String name,
      @RequestParam(value = "mealType", required = false) MealType mealType,
      @RequestParam(value = "countryOfOrigin", required = false) String countryOfOrigin,
      @RequestParam(value = "difficulty", required = false) Difficulty difficulty,
      Model model) {

    List<Recipe> recipes;

    if (name == null && mealType == null && countryOfOrigin == null && difficulty == null) {
      recipes = recipeService.fetchAllRecipes();
      model.addAttribute("noResults", false);
    } else {
      recipes = recipeService.searchRecipes(name, mealType, countryOfOrigin, difficulty);
      model.addAttribute("noResults", recipes.isEmpty());
    }

    Set<String> allCountries = recipeService.getAllCountries(recipes);

    model.addAttribute("recipes", recipes);
    model.addAttribute("countries", allCountries);
    model.addAttribute("name", name);
    model.addAttribute("mealType", mealType);
    model.addAttribute("countryOfOrigin", countryOfOrigin);
    model.addAttribute("difficulty", difficulty);
    return "recipes";
  }

  @GetMapping("/recipes/reset")
  public String resetSearch(Model model) {
    List<Recipe> recipes = recipeService.fetchAllRecipes();
    populateModelWithRecipesAndCountries(model, recipes);
    return "recipes";
  }

  private void populateModelWithRecipesAndCountries(Model model, List<Recipe> recipes) {
    Set<String> allCountries = recipeService.getAllCountries(recipes);
    model.addAttribute("recipes", recipes);
    model.addAttribute("noResults", recipes.isEmpty());
    model.addAttribute("countries", allCountries);
  }

}