package dev.rm.recipes.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.HttpMethod;

import org.springframework.http.MediaType;

import dev.rm.recipes.model.Difficulty;
import dev.rm.recipes.model.MealType;
import dev.rm.recipes.model.Recipe;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    // return List.of(response.getBody());

    List<Recipe> recipes = List.of(response.getBody());
    Set<String> countries = recipes.stream()
        .map(Recipe::getCountryOfOrigin)
        .filter(country -> country != null && !country.isEmpty())
        .collect(Collectors.toSet());

    return recipes;

  }

  public Recipe getRecipeById(Long id, String token) {
    HttpHeaders headers = new HttpHeaders();
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

  public List<Recipe> searchRecipes(String name, MealType mealType, String countryOfOrigin, Difficulty difficulty) {
    // UriComponentsBuilder uriBuilder =
    // UriComponentsBuilder.fromHttpUrl(backendUrl+ "/recipes/search")
    // .queryParam("name", name)
    // .queryParam("mealType", mealType)
    // .queryParam("countryOfOrigin", countryOfOrigin)
    // .queryParam("difficulty", difficulty);

    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(backendUrl + "/recipes/search");

    if (name != null && !name.isEmpty()) {
      uriBuilder.queryParam("name", name);
    }
    if (mealType != null) {
      uriBuilder.queryParam("mealType", mealType);
    }
    if (countryOfOrigin != null && !countryOfOrigin.isEmpty()) {
      uriBuilder.queryParam("countryOfOrigin", countryOfOrigin);
    }
    if (difficulty != null) {
      uriBuilder.queryParam("difficulty", difficulty);
    }

    ResponseEntity<Recipe[]> response = restTemplate.exchange(
        uriBuilder.toUriString(),
        HttpMethod.GET,
        null,
        Recipe[].class);

    return List.of(response.getBody());
  }

  public Set<String> getAllCountries(List<Recipe> recipes) {
    return recipes.stream()
        .map(Recipe::getCountryOfOrigin)
        .filter(country -> country != null && !country.isEmpty())
        .collect(Collectors.toSet());
  }

}