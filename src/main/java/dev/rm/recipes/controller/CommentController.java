package dev.rm.recipes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import dev.rm.recipes.model.Comment;
import dev.rm.recipes.model.Recipe;
import dev.rm.recipes.service.CommentService;
import dev.rm.recipes.service.RecipeService;
import dev.rm.recipes.utils.RecipeUtils;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/recipes")
public class CommentController {

  private final CommentService commentService;

  private final RecipeService recipeService;

  public CommentController(CommentService commentService, RecipeService recipeService) {
    this.commentService = commentService;
    this.recipeService = recipeService;
  }

  @GetMapping("/{id}/comments")
  public String getCommentsByRecipeId(@PathVariable Long id, Model model, HttpSession session) {

    String token = (String) session.getAttribute("token");
    if (token == null) {
      return "redirect:/login";
    }

    List<Comment> comments = commentService.getCommentsByRecipeId(id, token);
    model.addAttribute("comments", comments);
    return "recipe-detail";
  }

  @PostMapping("/{recipeId}/comments")
  public String addComment(@PathVariable Long recipeId, @RequestParam("content") String content,
      HttpSession session,
      Model model) {

    String token = (String) session.getAttribute("token");
    if (token == null || token.isEmpty()) {
      return "redirect:/login";
    }

    Comment comment = commentService.createComment(recipeId, content, token);

    Recipe recipe = recipeService.getRecipeById(recipeId, token);
    String videoId = RecipeUtils.extractVideoId(recipe.getVideoUrl());

    List<Comment> comments = commentService.getCommentsByRecipeId(recipeId, token);

    model.addAttribute("recipe", recipe);
    model.addAttribute("videoId", videoId);
    model.addAttribute("comments", comments);

    return "recipe-detail";

  }
}
