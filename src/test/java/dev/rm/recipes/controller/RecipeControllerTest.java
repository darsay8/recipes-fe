package dev.rm.recipes.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import dev.rm.recipes.model.Comment;
import dev.rm.recipes.model.Difficulty;
import dev.rm.recipes.model.MealType;
import dev.rm.recipes.model.Recipe;

import dev.rm.recipes.service.CommentService;
import dev.rm.recipes.service.LikeService;
import dev.rm.recipes.service.RecipeService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@WebMvcTest(RecipeController.class)
@ExtendWith(MockitoExtension.class)
public class RecipeControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private RecipeService recipeService;

  @MockBean
  private CommentService commentService;

  @MockBean
  private LikeService likeService;

  @InjectMocks
  private RecipeController recipeController;

  private MockHttpSession session;

  @BeforeEach
  public void setUp() {
    session = new MockHttpSession();
    session.setAttribute("token",
        "Bearer eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImphbWVzQG1haWwuY29tIiwidXNlcm5hbWUiOiJqYW1lcyIsInJvbGVzIjoiQURNSU4iLCJzdWIiOiJqYW1lc0BtYWlsLmNvbSIsImlhdCI6MTczMzM0NTAxNiwiZXhwIjoxNzMzNDMxNDE2fQ.bD8YpWMxe3aUSAbmzuCJdTakESQ9paWhKkZhJhY9aTM");
    mockMvc = MockMvcBuilders.standaloneSetup(recipeController).build();
  }

  @Test
  void getRecipes_ShouldReturnRecipesView() throws Exception {

    List<Recipe> recipes = List.of(new Recipe(1L, "Pasta", "pasta.jpg", "video_url", MealType.LUNCH, null, "Italy",
        Difficulty.MEDIUM, "Boil pasta", null));
    when(recipeService.fetchAllRecipes()).thenReturn(recipes);

    mockMvc.perform(MockMvcRequestBuilders.get("/")
        .session(session))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.view().name("recipes"))
        .andExpect(MockMvcResultMatchers.model().attributeExists("recipes",
            "countries", "noResults"))
        .andExpect(MockMvcResultMatchers.model().attribute("recipes", recipes));
    ;
  }

  @Test
  void getRecipeDetail_ShouldReturnRecipeDetailView() throws Exception {

    Recipe recipe = new Recipe(1L, "Pasta", "pasta.jpg", "video_url", MealType.LUNCH, null, "Italy", Difficulty.MEDIUM,
        "Boil pasta", null);
    when(recipeService.getRecipeById(1L)).thenReturn(recipe);

    List<Comment> comments = new ArrayList<>();
    when(commentService.getCommentsByRecipeId(1L)).thenReturn(comments);

    when(likeService.getLikesCountByRecipeId(1L)).thenReturn(10L);

    mockMvc.perform(MockMvcRequestBuilders.get("/recipes/{id}", 1L)
        .session(session))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.view().name("recipe-detail"))
        .andExpect(MockMvcResultMatchers.model().attributeExists("recipe", "comments", "likes"))
        .andExpect(MockMvcResultMatchers.model().attribute("recipe", recipe));
  }

  @Test
  public void testGetRecipeDetail_NoToken() throws Exception {
    session.clearAttributes();

    mockMvc.perform(MockMvcRequestBuilders.get("/recipes/{id}", 1L)
        .session(session))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
        .andExpect(MockMvcResultMatchers.redirectedUrl("/login"));

  }

  @Test
  public void testSearchRecipes_Success() throws Exception {

    List<Recipe> recipes = new ArrayList<>();
    when(recipeService.searchRecipes("Test", null, null, null)).thenReturn(recipes);

    Set<String> countries = Set.of("USA", "Canada");
    when(recipeService.getAllCountries(recipes)).thenReturn(countries);

    mockMvc.perform(MockMvcRequestBuilders.get("/recipes/search")
        .session(session)
        .param("name", "Test"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.view().name("recipes"))
        .andExpect(MockMvcResultMatchers.model().attributeExists("recipes", "countries", "noResults", "name"));
  }

  @Test
  public void testResetSearch() throws Exception {
    List<Recipe> recipes = new ArrayList<>();
    when(recipeService.fetchAllRecipes()).thenReturn(recipes);

    Set<String> countries = Set.of("USA", "Canada");
    when(recipeService.getAllCountries(recipes)).thenReturn(countries);

    mockMvc.perform(MockMvcRequestBuilders.get("/recipes/reset")
        .session(session))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.view().name("recipes"))
        .andExpect(MockMvcResultMatchers.model().attributeExists("recipes", "countries", "noResults"));
  }

  @Test
  public void testShowCreateRecipeForm_Success() throws Exception {

    mockMvc.perform(MockMvcRequestBuilders.get("/recipes/create")
        .session(session))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.view().name("create-recipe"))
        .andExpect(MockMvcResultMatchers.model().attributeExists("recipe"));
  }

  @Test
  public void testShowCreateRecipeForm_NoToken() throws Exception {

    session.clearAttributes();

    mockMvc.perform(MockMvcRequestBuilders.get("/recipes/create")
        .session(session))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
        .andExpect(MockMvcResultMatchers.redirectedUrl("/login"));
  }

  @Test
  void createRecipe_ShouldRedirectToRecipes_WhenCreatedSuccessfully() throws Exception {

    when(recipeService.createRecipe(any(Recipe.class))).thenReturn(true);

    mockMvc.perform(MockMvcRequestBuilders.post("/recipes/create")
        .session(session)
        .param("name", "Pasta")
        .param("image", "pasta.jpg")
        .param("videoUrl", "video_url")
        .param("mealType", "LUNCH")
        .param("difficulty", "MEDIUM")
        .param("instructions", "Boil pasta")
        .param("countryOfOrigin", "Italy")
        .param("ingredients[]", "Spaghetti")
        .param("quantities[]", "200 grams")
        .param("comments[]", "")
        .param("likes[]", ""))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
        .andExpect(MockMvcResultMatchers.redirectedUrl("/recipes"));
  }

  @Test
  public void testSearchRecipes_NoFilters() throws Exception {

    List<Recipe> recipes = List.of(new Recipe(1L, "Pasta", "pasta.jpg", "video_url", MealType.LUNCH, null, "Italy",
        Difficulty.MEDIUM, "Boil pasta", null));
    when(recipeService.fetchAllRecipes()).thenReturn(recipes);

    Set<String> countries = Set.of("Italy");
    when(recipeService.getAllCountries(recipes)).thenReturn(countries);

    mockMvc.perform(MockMvcRequestBuilders.get("/recipes/search")
        .session(session))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.view().name("recipes"))
        .andExpect(MockMvcResultMatchers.model().attributeExists("recipes", "countries", "noResults"))
        .andExpect(MockMvcResultMatchers.model().attribute("noResults", false));
  }

  @Test
  public void testSearchRecipes_WithNameFilter() throws Exception {

    List<Recipe> recipes = List.of(new Recipe(1L, "Pasta", "pasta.jpg", "video_url", MealType.LUNCH, null, "Italy",
        Difficulty.MEDIUM, "Boil pasta", null));
    when(recipeService.searchRecipes("Pasta", null, null, null)).thenReturn(recipes);

    Set<String> countries = Set.of("Italy");
    when(recipeService.getAllCountries(recipes)).thenReturn(countries);

    mockMvc.perform(MockMvcRequestBuilders.get("/recipes/search")
        .session(session)
        .param("name", "Pasta"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.view().name("recipes"))
        .andExpect(MockMvcResultMatchers.model().attributeExists("recipes", "countries", "noResults"))
        .andExpect(MockMvcResultMatchers.model().attribute("recipes", recipes));
  }

  @Test
  public void testSearchRecipes_WithMealTypeFilter() throws Exception {

    List<Recipe> recipes = List.of(new Recipe(1L, "Pasta", "pasta.jpg", "video_url", MealType.LUNCH, null, "Italy",
        Difficulty.MEDIUM, "Boil pasta", null));
    when(recipeService.searchRecipes(null, MealType.LUNCH, null, null)).thenReturn(recipes);

    Set<String> countries = Set.of("Italy");
    when(recipeService.getAllCountries(recipes)).thenReturn(countries);

    mockMvc.perform(MockMvcRequestBuilders.get("/recipes/search")
        .session(session)
        .param("mealType", "LUNCH"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.view().name("recipes"))
        .andExpect(MockMvcResultMatchers.model().attributeExists("recipes", "countries", "noResults"))
        .andExpect(MockMvcResultMatchers.model().attribute("recipes", recipes));
  }

  @Test
  public void testSearchRecipes_WithCountryOfOriginFilter() throws Exception {

    List<Recipe> recipes = List.of(new Recipe(1L, "Pasta", "pasta.jpg", "video_url", MealType.LUNCH, null, "Italy",
        Difficulty.MEDIUM, "Boil pasta", null));
    when(recipeService.searchRecipes(null, null, "Italy", null)).thenReturn(recipes);

    Set<String> countries = Set.of("Italy");
    when(recipeService.getAllCountries(recipes)).thenReturn(countries);

    mockMvc.perform(MockMvcRequestBuilders.get("/recipes/search")
        .session(session)
        .param("countryOfOrigin", "Italy"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.view().name("recipes"))
        .andExpect(MockMvcResultMatchers.model().attributeExists("recipes", "countries", "noResults"))
        .andExpect(MockMvcResultMatchers.model().attribute("recipes", recipes));
  }

  @Test
  public void testSearchRecipes_WithDifficultyFilter() throws Exception {

    List<Recipe> recipes = List.of(new Recipe(1L, "Pasta", "pasta.jpg", "video_url", MealType.LUNCH, null, "Italy",
        Difficulty.MEDIUM, "Boil pasta", null));
    when(recipeService.searchRecipes(null, null, null, Difficulty.MEDIUM)).thenReturn(recipes);

    Set<String> countries = Set.of("Italy");
    when(recipeService.getAllCountries(recipes)).thenReturn(countries);

    mockMvc.perform(MockMvcRequestBuilders.get("/recipes/search")
        .session(session)
        .param("difficulty", "MEDIUM"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.view().name("recipes"))
        .andExpect(MockMvcResultMatchers.model().attributeExists("recipes", "countries", "noResults"))
        .andExpect(MockMvcResultMatchers.model().attribute("recipes", recipes));
  }

  @Test
  public void testSearchRecipes_WithNoResults() throws Exception {

    when(recipeService.searchRecipes("NonExistentRecipe", null, null, null)).thenReturn(new ArrayList<>());

    Set<String> countries = new HashSet<>();
    when(recipeService.getAllCountries(new ArrayList<>())).thenReturn(countries);

    mockMvc.perform(MockMvcRequestBuilders.get("/recipes/search")
        .session(session)
        .param("name", "NonExistentRecipe"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.view().name("recipes"))
        .andExpect(MockMvcResultMatchers.model().attribute("noResults", true));
  }

  @Test
  public void testSearchRecipes_WithMultipleFilters() throws Exception {

    List<Recipe> recipes = List.of(new Recipe(1L, "Pasta", "pasta.jpg", "video_url", MealType.LUNCH, null, "Italy",
        Difficulty.MEDIUM, "Boil pasta", null));
    when(recipeService.searchRecipes("Pasta", MealType.LUNCH, "Italy", Difficulty.MEDIUM)).thenReturn(recipes);

    Set<String> countries = Set.of("Italy");
    when(recipeService.getAllCountries(recipes)).thenReturn(countries);

    mockMvc.perform(MockMvcRequestBuilders.get("/recipes/search")
        .session(session)
        .param("name", "Pasta")
        .param("mealType", "LUNCH")
        .param("countryOfOrigin", "Italy")
        .param("difficulty", "MEDIUM"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.view().name("recipes"))
        .andExpect(MockMvcResultMatchers.model().attributeExists("recipes", "countries", "noResults"))
        .andExpect(MockMvcResultMatchers.model().attribute("recipes", recipes));
  }

  @Test
  public void testCreateRecipe_NoToken() throws Exception {

    session.clearAttributes();

    mockMvc.perform(MockMvcRequestBuilders.post("/recipes/create")
        .session(session)
        .param("name", "Pasta")
        .param("image", "pasta.jpg")
        .param("videoUrl", "video_url")
        .param("mealType", "LUNCH")
        .param("difficulty", "MEDIUM")
        .param("instructions", "Boil pasta")
        .param("countryOfOrigin", "Italy")
        .param("ingredients[]", "Spaghetti")
        .param("quantities[]", "200 grams"))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
        .andExpect(MockMvcResultMatchers.redirectedUrl("/login"));
  }

  @Test
  public void testCreateRecipe_EmptyFields() throws Exception {

    when(recipeService.createRecipe(any(Recipe.class))).thenReturn(false);

    mockMvc.perform(MockMvcRequestBuilders.post("/recipes/create")
        .session(session)
        .param("name", "") // Empty name
        .param("image", "") // Empty image
        .param("videoUrl", "")
        .param("mealType", "LUNCH")
        .param("difficulty", "MEDIUM")
        .param("instructions", "Boil pasta")
        .param("countryOfOrigin", "Italy")
        .param("ingredients[]", "Spaghetti")
        .param("quantities[]", "200 grams"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.view().name("create-recipe"))
        .andExpect(MockMvcResultMatchers.model().attributeExists("error"));
  }

  @Test
  public void testCreateRecipe_SuccessfulCreation() throws Exception {

    when(recipeService.createRecipe(any(Recipe.class))).thenReturn(true);

    mockMvc.perform(MockMvcRequestBuilders.post("/recipes/create")
        .session(session)
        .param("name", "Pasta")
        .param("image", "pasta.jpg")
        .param("videoUrl", "video_url")
        .param("mealType", "LUNCH")
        .param("difficulty", "MEDIUM")
        .param("instructions", "Boil pasta")
        .param("countryOfOrigin", "Italy")
        .param("ingredients[]", "Spaghetti")
        .param("quantities[]", "200 grams"))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
        .andExpect(MockMvcResultMatchers.redirectedUrl("/recipes"));
  }

  @Test
  public void testCreateRecipe_FailedCreation() throws Exception {

    when(recipeService.createRecipe(any(Recipe.class))).thenReturn(false);

    mockMvc.perform(MockMvcRequestBuilders.post("/recipes/create")
        .session(session)
        .param("name", "Pasta")
        .param("image", "pasta.jpg")
        .param("videoUrl", "video_url")
        .param("mealType", "LUNCH")
        .param("difficulty", "MEDIUM")
        .param("instructions", "Boil pasta")
        .param("countryOfOrigin", "Italy")
        .param("ingredients[]", "Spaghetti")
        .param("quantities[]", "200 grams"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.view().name("create-recipe"))
        .andExpect(MockMvcResultMatchers.model().attribute("error", "Failed to create recipe."));
  }

}