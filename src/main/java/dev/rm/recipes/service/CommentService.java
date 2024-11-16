package dev.rm.recipes.service;

import org.springframework.beans.factory.annotation.Value;
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

  public List<Comment> getCommentsByRecipeId(Long recipeId, String token) {

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + token);
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<Void> entity = new HttpEntity<>(headers);

    ResponseEntity<List<Comment>> response = restTemplate.exchange(
        backendUrl + "/recipes/" + recipeId + "/comments", HttpMethod.GET, entity,
        (Class<List<Comment>>) (Class<?>) List.class);

    return response.getBody();
  }

  public Comment createComment(Long recipeId, String content, String token) {
    Comment comment = new Comment();
    comment.setContent(content);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", "Bearer " + token);

    HttpEntity<Comment> request = new HttpEntity<>(comment, headers);

    ResponseEntity<Comment> response = restTemplate.exchange(
        backendUrl + "/recipes/" + recipeId + "/comments", HttpMethod.POST, request, Comment.class);

    return response.getBody();
  }
}
