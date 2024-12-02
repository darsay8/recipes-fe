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

  public LikeService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public long getLikesCountByRecipeId(Long recipeId) {

    ResponseEntity<Long> response = restTemplate.exchange(
        backendUrl + "/recipes/" + recipeId + "/likes/total", HttpMethod.GET, null,
        Long.class);

    return response.getBody();
  }

  public Like createLike(Long recipeId) {
    Like like = new Like();

    HttpEntity<Like> request = new HttpEntity<>(like);

    ResponseEntity<Like> response = restTemplate.exchange(
        backendUrl + "/recipes/" + recipeId + "/likes", HttpMethod.POST, request, Like.class);

    return response.getBody();
  }

}
