package dev.rm.recipes.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import dev.rm.recipes.model.Like;

@Service
public class LikeService {
  @Value("${backend.url}")
  private String backendUrl;

  private final RestTemplate restTemplate;

  public void setBackendUrl(String backendUrl) {
    this.backendUrl = backendUrl;
  }

  public LikeService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public long getLikesCountByRecipeId(Long recipeId) {

    ResponseEntity<Long> response = restTemplate.exchange(
        backendUrl + "/recipes/" + recipeId + "/likes/total", HttpMethod.GET, null,
        Long.class);

    Long likesCount = response.getBody();
    return (likesCount != null) ? likesCount : 0L;
  }

  public Like createLike(Long recipeId) {
    Like like = new Like();

    HttpEntity<Like> request = new HttpEntity<>(like);

    ResponseEntity<Like> response = restTemplate.exchange(
        backendUrl + "/recipes/" + recipeId + "/likes", HttpMethod.POST, request, Like.class);

    if (response.getStatusCode() != HttpStatus.CREATED) {
      throw new RuntimeException("Failed to create like");
    }

    return response.getBody();
  }

}
