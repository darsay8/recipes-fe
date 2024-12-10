package dev.rm.recipes.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import dev.rm.recipes.model.Difficulty;
import dev.rm.recipes.model.MealType;
import dev.rm.recipes.model.Recipe;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class RecipeServiceTests {

  @Mock
  private RestTemplate restTemplate;

  @InjectMocks
  private RecipeService recipeService;

  private static final String BASE_URL = "http://localhost:8092/";

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    recipeService.setBackendUrl(BASE_URL);
  }

  @Test
  void fetchAllRecipes_ShouldReturnRecipes() {

    Recipe[] recipesArray = new Recipe[] {
        new Recipe(1L, "Pasta", "pasta.jpg", "video_url", MealType.LUNCH, null, "Italy", Difficulty.MEDIUM,
            "Boil pasta", null)
    };
    ResponseEntity<Recipe[]> responseEntity = ResponseEntity.ok(recipesArray);
    when(restTemplate.exchange(eq(BASE_URL + "/recipes"), eq(HttpMethod.GET), isNull(), eq(Recipe[].class)))
        .thenReturn(responseEntity);

    List<Recipe> recipes = recipeService.fetchAllRecipes();

    assertNotNull(recipes);
    assertEquals(1, recipes.size());
    assertEquals("Pasta", recipes.get(0).getName());
  }

  @Test
  void getRecipeById_ShouldReturnRecipe_WhenFound() {

    Recipe recipe = new Recipe(1L, "Pasta", "pasta.jpg", "video_url", MealType.LUNCH, null, "Italy", Difficulty.MEDIUM,
        "Boil pasta", null);
    ResponseEntity<Recipe> responseEntity = ResponseEntity.ok(recipe);
    when(restTemplate.exchange(eq(BASE_URL + "/recipes/1"), eq(HttpMethod.GET), isNull(), eq(Recipe.class)))
        .thenReturn(responseEntity);

    Recipe result = recipeService.getRecipeById(1L);

    assertNotNull(result);
    assertEquals("Pasta", result.getName());
  }

  @Test
  void getRecipeById_ShouldReturnNull_WhenNotFound() {

    when(restTemplate.exchange(eq(BASE_URL + "/recipes/999"), eq(HttpMethod.GET), isNull(), eq(Recipe.class)))
        .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

    Recipe result = recipeService.getRecipeById(999L);

    assertNull(result);
  }

  @Test
  void searchRecipes_ShouldReturnFilteredResults() {

    Recipe[] recipesArray = new Recipe[] {
        new Recipe(1L, "Pasta", "pasta.jpg", "video_url", MealType.LUNCH, null, "Italy", Difficulty.MEDIUM,
            "Boil pasta", null)
    };
    ResponseEntity<Recipe[]> responseEntity = ResponseEntity.ok(recipesArray);

    when(restTemplate.exchange(
        contains("/recipes/search?name=Pasta"),
        eq(HttpMethod.GET),
        isNull(),
        eq(Recipe[].class)))
        .thenReturn(responseEntity);

    List<Recipe> recipes = recipeService.searchRecipes("Pasta", MealType.LUNCH, "Italy", Difficulty.MEDIUM);

    assertNotNull(recipes);
    assertEquals(1, recipes.size());
    assertEquals("Pasta", recipes.get(0).getName());

    verify(restTemplate, times(1)).exchange(
        contains("/recipes/search?name=Pasta"),
        eq(HttpMethod.GET),
        isNull(),
        eq(Recipe[].class));
  }

  @Test
  void createRecipe_ShouldReturnTrue_WhenSuccess() {

    Recipe recipe = new Recipe(1L, "Pasta", "pasta.jpg", "video_url", MealType.LUNCH, null, "Italy", Difficulty.MEDIUM,
        "Boil pasta", null);
    HttpEntity<Recipe> entity = new HttpEntity<>(recipe);
    ResponseEntity<Map<String, Object>> responseEntity = ResponseEntity.status(HttpStatus.CREATED)
        .body(Map.of("id", 1L));
    when(restTemplate.exchange(eq(BASE_URL + "/recipes"), eq(HttpMethod.POST), eq(entity),
        eq(new ParameterizedTypeReference<Map<String, Object>>() {
        })))
        .thenReturn(responseEntity);

    boolean isCreated = recipeService.createRecipe(recipe);

    assertTrue(isCreated);
  }

  @Test
  void createRecipe_ShouldReturnFalse_WhenFailure() {

    Recipe recipe = new Recipe(1L, "Pasta", "pasta.jpg", "video_url", MealType.LUNCH, null, "Italy", Difficulty.MEDIUM,
        "Boil pasta", null);
    HttpEntity<Recipe> entity = new HttpEntity<>(recipe);
    ResponseEntity<Map<String, Object>> responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(Map.of("error", "Failed"));
    when(restTemplate.exchange(eq(BASE_URL + "/recipes"), eq(HttpMethod.POST), eq(entity),
        eq(new ParameterizedTypeReference<Map<String, Object>>() {
        })))
        .thenReturn(responseEntity);

    boolean isCreated = recipeService.createRecipe(recipe);

    assertFalse(isCreated);
  }

  @Test
  void getAllCountries_ShouldReturnUniqueCountries() {

    List<Recipe> recipes = Arrays.asList(
        new Recipe(1L, "Pasta", "pasta.jpg", "video_url", MealType.LUNCH, null, "Italy", Difficulty.MEDIUM,
            "Boil pasta", null),
        new Recipe(2L, "Pizza", "pizza.jpg", "video_url", MealType.DINNER, null, "Italy", Difficulty.HARD, "Bake pizza",
            null),
        new Recipe(3L, "Sushi", "sushi.jpg", "video_url", MealType.DINNER, null, "Japan", Difficulty.MEDIUM,
            "Roll sushi", null));

    Set<String> countries = recipeService.getAllCountries(recipes);

    assertEquals(2, countries.size());
    assertTrue(countries.contains("Italy"));
    assertTrue(countries.contains("Japan"));
  }

  @Test
  void getAllCountries_ShouldSkipEmptyOrNullCountries() {

    List<Recipe> recipes = Arrays.asList(
        new Recipe(1L, "Pasta", "pasta.jpg", "video_url", MealType.LUNCH, null, "Italy", Difficulty.MEDIUM,
            "Boil pasta", null),
        new Recipe(2L, "Pizza", "pizza.jpg", "video_url", MealType.DINNER, null, "", Difficulty.HARD, "Bake pizza",
            null),
        new Recipe(3L, "Sushi", "sushi.jpg", "video_url", MealType.DINNER, null, null, Difficulty.MEDIUM, "Roll sushi",
            null));

    Set<String> countries = recipeService.getAllCountries(recipes);

    assertEquals(1, countries.size());
    assertTrue(countries.contains("Italy"));
  }

  @Test
  void getAllCountries_ShouldReturnEmptySet_WhenNoValidCountries() {

    List<Recipe> recipes = Arrays.asList(
        new Recipe(1L, "Pasta", "pasta.jpg", "video_url", MealType.LUNCH, null, "", Difficulty.MEDIUM, "Boil pasta",
            null),
        new Recipe(2L, "Pizza", "pizza.jpg", "video_url", MealType.DINNER, null, null, Difficulty.HARD, "Bake pizza",
            null));

    Set<String> countries = recipeService.getAllCountries(recipes);

    assertTrue(countries.isEmpty());
  }

  @Test
  void getAllCountries_ShouldHandleEmptyList() {

    List<Recipe> recipes = Collections.emptyList();

    Set<String> countries = recipeService.getAllCountries(recipes);

    assertTrue(countries.isEmpty());
  }

  @Test
  void searchRecipes_ShouldReturnFilteredResults_WithPartialParams() {

    Recipe[] recipesArray = new Recipe[] {
        new Recipe(1L, "Pasta", "pasta.jpg", "video_url", MealType.LUNCH, null, "Italy", Difficulty.MEDIUM,
            "Boil pasta", null)
    };
    ResponseEntity<Recipe[]> responseEntity = ResponseEntity.ok(recipesArray);

    when(restTemplate.exchange(
        contains("/recipes/search?name=Pasta&mealType=LUNCH"),
        eq(HttpMethod.GET),
        isNull(),
        eq(Recipe[].class)))
        .thenReturn(responseEntity);

    List<Recipe> recipes = recipeService.searchRecipes("Pasta", MealType.LUNCH, null, null);

    assertNotNull(recipes);
    assertEquals(1, recipes.size());
    assertEquals("Pasta", recipes.get(0).getName());

    verify(restTemplate, times(1)).exchange(
        contains("/recipes/search?name=Pasta&mealType=LUNCH"),
        eq(HttpMethod.GET),
        isNull(),
        eq(Recipe[].class));
  }

  @Test
  void searchRecipes_ShouldReturnEmptyList_WhenNoResultsFound() {

    Recipe[] recipesArray = new Recipe[] {};
    ResponseEntity<Recipe[]> responseEntity = ResponseEntity.ok(recipesArray);

    when(restTemplate.exchange(
        contains("/recipes/search?name=NonExistentName"),
        eq(HttpMethod.GET),
        isNull(),
        eq(Recipe[].class)))
        .thenReturn(responseEntity);

    List<Recipe> recipes = recipeService.searchRecipes("NonExistentName", null, null, null);

    assertNotNull(recipes);
    assertTrue(recipes.isEmpty());
  }

}
