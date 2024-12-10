package dev.rm.recipes.controller;

import static org.mockito.Mockito.*;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
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
import org.springframework.test.web.servlet.ResultActions;

import dev.rm.recipes.model.Difficulty;
import dev.rm.recipes.model.Like;
import dev.rm.recipes.model.MealType;
import dev.rm.recipes.model.Recipe;
import dev.rm.recipes.service.LikeService;
import dev.rm.recipes.service.RecipeService;

@WebMvcTest(LikeController.class)
@ExtendWith(MockitoExtension.class)
public class LikeControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private LikeService likeService;

  @MockBean
  private RecipeService recipeService;

  @InjectMocks
  private LikeController likeController;

  private MockHttpSession session;

  @BeforeEach
  public void setUp() {
    session = new MockHttpSession();
    session.setAttribute("token",
        "Bearer eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImphbWVzQG1haWwuY29tIiwidXNlcm5hbWUiOiJqYW1lcyIsInJvbGVzIjoiQURNSU4iLCJzdWIiOiJqYW1lc0BtYWlsLmNvbSIsImlhdCI6MTczMzM0NTAxNiwiZXhwIjoxNzMzNDMxNDE2fQ.bD8YpWMxe3aUSAbmzuCJdTakESQ9paWhKkZhJhY9aTM");
    mockMvc = MockMvcBuilders.standaloneSetup(likeController).build();
  }

  @Test
  void likeRecipe_ShouldRedirectToLogin_WhenNoToken() throws Exception {
    // Clear the session token
    session.clearAttributes();

    // Act & Assert
    mockMvc.perform(MockMvcRequestBuilders.post("/recipes/{recipeId}/likes", 1L)
        .session(session))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
        .andExpect(MockMvcResultMatchers.redirectedUrl("/login"));
  }

  @Test
  void testGetRecipeWithLikes_NoToken() throws Exception {
    Long recipeId = 1L;

    // Mocking a session without a token
    MockHttpSession session = new MockHttpSession();

    // Perform the GET request
    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/recipes/{recipeId}/likes", recipeId)
        .session(session));

    // Verify the response redirects to login
    result.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
        .andExpect(MockMvcResultMatchers.header().string("Location", "/login"));
  }

  @Test
  void testLikeRecipe_NoToken() throws Exception {
    Long recipeId = 1L;

    // Mocking a session without a token
    MockHttpSession session = new MockHttpSession();

    // Perform the POST request
    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/recipes/{recipeId}/likes", recipeId)
        .session(session));

    // Verify the response redirects to login
    result.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
        .andExpect(MockMvcResultMatchers.header().string("Location", "/login"));
  }

}
