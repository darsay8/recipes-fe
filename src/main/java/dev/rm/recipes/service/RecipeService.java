package dev.rm.recipes.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpMethod;

import org.springframework.http.MediaType;

import dev.rm.recipes.model.Recipe;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
public class RecipeService {

  @Value("${backend.url}")
  private String backendUrl;

  private final RestTemplate restTemplate;

  public RecipeService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public List<Recipe> fetchAllRecipes() {
    HttpHeaders headers = new HttpHeaders();
    HttpEntity<String> entity = new HttpEntity<>(headers);

    ResponseEntity<Recipe[]> response = restTemplate.exchange(
        backendUrl + "/recipes",
        HttpMethod.GET,
        entity,
        Recipe[].class);

    return List.of(response.getBody());
  }

  public Recipe getRecipeById(Long id, String token) {
    HttpHeaders headers = new HttpHeaders();
    // Just add the token to the Authorization header
    headers.set("Authorization", "Bearer " + token);
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<Void> entity = new HttpEntity<>(headers);

    try {
      ResponseEntity<Recipe> response = restTemplate.exchange(
          backendUrl + "/recipes/" + id,
          HttpMethod.GET,
          entity,
          Recipe.class);
      return response.getBody();
    } catch (HttpClientErrorException e) {
      log.error("Error accessing recipe: {}", e.getMessage());
      return null;
    }
  }

}