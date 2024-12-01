package dev.rm.recipes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import dev.rm.recipes.model.Comment;
import dev.rm.recipes.model.Difficulty;
import dev.rm.recipes.model.Ingredient;
import dev.rm.recipes.model.MealType;
import dev.rm.recipes.model.Recipe;
import dev.rm.recipes.service.CommentService;
import dev.rm.recipes.service.LikeService;
import dev.rm.recipes.service.RecipeService;
import dev.rm.recipes.utils.RecipeUtils;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Controller
public class RecipeController {

  private RecipeService recipeService;
  private CommentService commentService;
  private LikeService likeService;

  public RecipeController(RecipeService recipeService, CommentService commentService, LikeService likeService) {
    this.recipeService = recipeService;
    this.commentService = commentService;
    this.likeService = likeService;
  }

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

    List<Comment> comments = commentService.getCommentsByRecipeId(id, token);
    String videoId = RecipeUtils.extractVideoId(recipe.getVideoUrl());

    long likes = likeService.getLikesCountByRecipeId(id, token);

    model.addAttribute("recipe", recipe);
    model.addAttribute("videoId", videoId);
    model.addAttribute("comments", comments);
    model.addAttribute("likes", likes);

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

  @GetMapping("/recipes/create")
  public String showCreateRecipeForm(Model model, HttpSession session) {
    String token = (String) session.getAttribute("token");
    if (token == null) {
      return "redirect:/login";
    }
    Recipe recipe = new Recipe();
    model.addAttribute("recipe", recipe);
    return "create-recipe";
  }

  @PostMapping("/recipes/create")
  public String createRecipe(
      @RequestParam("name") String name,
      @RequestParam("image") String image,
      @RequestParam("videoUrl") String videoUrl,
      @RequestParam("mealType") MealType mealType,
      @RequestParam("difficulty") Difficulty difficulty,
      @RequestParam("instructions") String instructions,
      @RequestParam("countryOfOrigin") String countryOfOrigin,
      @RequestParam("ingredients[]") String[] ingredientNames,
      @RequestParam("quantities[]") String[] ingredientQuantities,
      HttpSession session, Model model) {

    String token = (String) session.getAttribute("token");
    if (token == null || token.isEmpty()) {
      return "redirect:/login";
    }

    List<Ingredient> ingredients = new ArrayList<>();
    for (int i = 0; i < ingredientNames.length; i++) {
      Ingredient ingredient = new Ingredient();
      ingredient.setName(ingredientNames[i]);
      ingredient.setQuantity(ingredientQuantities[i]);
      ingredients.add(ingredient);
    }

    Recipe recipe = new Recipe();
    recipe.setName(name);
    recipe.setImage(image);
    recipe.setVideoUrl(videoUrl);
    recipe.setMealType(mealType);
    recipe.setDifficulty(difficulty);
    recipe.setInstructions(instructions);
    recipe.setCountryOfOrigin(countryOfOrigin);
    recipe.setIngredients(ingredients);

    boolean isCreated = recipeService.createRecipe(recipe, token);
    if (isCreated) {
      return "redirect:/recipes";
    } else {
      model.addAttribute("error", "Failed to create recipe.");
      return "create-recipe";
    }
  }

  private void populateModelWithRecipesAndCountries(Model model, List<Recipe> recipes) {
    Set<String> allCountries = recipeService.getAllCountries(recipes);
    model.addAttribute("recipes", recipes);
    model.addAttribute("noResults", recipes.isEmpty());
    model.addAttribute("countries", allCountries);
  }

}