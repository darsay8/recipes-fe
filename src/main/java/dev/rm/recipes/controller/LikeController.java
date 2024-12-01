package dev.rm.recipes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.ui.Model;

import dev.rm.recipes.model.Recipe;
import dev.rm.recipes.service.LikeService;
import dev.rm.recipes.service.RecipeService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
@RequestMapping("/recipes")
public class LikeController {

  private final LikeService likeService;
  private final RecipeService recipeService;

  public LikeController(LikeService likeService, RecipeService recipeService) {
    this.likeService = likeService;
    this.recipeService = recipeService;
  }

  @GetMapping("/{recipeId}/likes")
  public String getRecipeWithLikes(@PathVariable Long id, Model model, HttpSession session) {

    String token = (String) session.getAttribute("token");
    if (token == null) {
      return "redirect:/login";
    }

    long likes = likeService.getLikesCountByRecipeId(id, token);

    model.addAttribute("likes", likes);

    return "recipe-detail";
  }

  @PostMapping("/{recipeId}/likes")
  public String likeRecipe(@PathVariable Long recipeId, HttpSession session, Model model) {

    String token = (String) session.getAttribute("token");
    if (token == null || token.isEmpty()) {
      return "redirect:/login";
    }

    try {
      likeService.createLike(recipeId, token);
      prepareModelForLike(recipeId, token, model);
      return "recipe-detail";
    } catch (Exception e) {
      prepareModelForLike(recipeId, token, model);
      return "recipe-detail";
    }
  }

  private void prepareModelForLike(Long recipeId, String token, Model model) {
    Recipe recipe = recipeService.getRecipeById(recipeId, token);
    long likes = likeService.getLikesCountByRecipeId(recipeId, token);

    model.addAttribute("recipe", recipe);
    model.addAttribute("likes", likes);
  }
}
