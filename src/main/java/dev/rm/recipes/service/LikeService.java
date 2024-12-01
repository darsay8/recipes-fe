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

  public long getLikesCountByRecipeId(Long recipeId, String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + token);
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<Void> entity = new HttpEntity<>(headers);

    ResponseEntity<Long> response = restTemplate.exchange(
        backendUrl + "/recipes/" + recipeId + "/likes/total", HttpMethod.GET, entity,
        Long.class);

    return response.getBody();
  }

  public Like createLike(Long recipeId, String token) {
    Like like = new Like();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", "Bearer " + token);

    HttpEntity<Like> request = new HttpEntity<>(like, headers);

    ResponseEntity<Like> response = restTemplate.exchange(
        backendUrl + "/recipes/" + recipeId + "/likes", HttpMethod.POST, request, Like.class);

    return response.getBody();
  }

}
