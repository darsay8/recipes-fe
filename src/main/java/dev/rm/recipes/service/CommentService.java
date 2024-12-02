package dev.rm.recipes.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import dev.rm.recipes.model.Comment;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
public class CommentService {

  @Value("${backend.url}")
  private String backendUrl;

  private final RestTemplate restTemplate;

  public CommentService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public List<Comment> getCommentsByRecipeId(Long recipeId) {
    ResponseEntity<List<Comment>> response = restTemplate.exchange(
        backendUrl + "/recipes/" + recipeId + "/comments",
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<Comment>>() {
        });

    return response.getBody();
  }

  public Comment createComment(Long recipeId, String content) {
    Comment comment = new Comment();
    comment.setContent(content);

    HttpEntity<Comment> request = new HttpEntity<>(comment);

    ResponseEntity<Comment> response = restTemplate.exchange(
        backendUrl + "/recipes/" + recipeId + "/comments", HttpMethod.POST, request, Comment.class);

    return response.getBody();
  }
}
