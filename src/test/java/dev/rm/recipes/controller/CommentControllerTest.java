package dev.rm.recipes.controller;

import static org.mockito.Mockito.*;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

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

import dev.rm.recipes.model.Comment;
import dev.rm.recipes.model.Difficulty;
import dev.rm.recipes.model.MealType;
import dev.rm.recipes.model.Recipe;
import dev.rm.recipes.service.CommentService;
import dev.rm.recipes.service.RecipeService;

@WebMvcTest(CommentController.class)
@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private CommentService commentService;

  @MockBean
  private RecipeService recipeService;

  @InjectMocks
  private CommentController commentController;

  private MockHttpSession session;

  @BeforeEach
  public void setUp() {
    session = new MockHttpSession();
    session.setAttribute("token",
        "Bearer eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImphbWVzQG1haWwuY29tIiwidXNlcm5hbWUiOiJqYW1lcyIsInJvbGVzIjoiQURNSU4iLCJzdWIiOiJqYW1lc0BtYWlsLmNvbSIsImlhdCI6MTczMzM0NTAxNiwiZXhwIjoxNzMzNDMxNDE2fQ.bD8YpWMxe3aUSAbmzuCJdTakESQ9paWhKkZhJhY9aTM");
    mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
  }

  @Test
  void getCommentsByRecipeId_ShouldReturnCommentsView() throws Exception {
    // Arrange
    Long recipeId = 1L;
    List<Comment> comments = List.of(new Comment("Looks great!", "user1"));
    when(commentService.getCommentsByRecipeId(recipeId)).thenReturn(comments);

    // Act & Assert
    mockMvc.perform(MockMvcRequestBuilders.get("/recipes/{id}/comments", recipeId)
        .session(session))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.view().name("recipe-detail"))
        .andExpect(MockMvcResultMatchers.model().attribute("comments", comments));
  }

  @Test
  void addComment_ShouldReturnRecipeDetail_WhenSuccess() throws Exception {
    // Arrange
    Long recipeId = 1L;
    String content = "Awesome recipe!";
    Comment newComment = new Comment(content, "user1");

    // Mock the recipeService to return a valid recipe
    Recipe recipe = new Recipe(recipeId, "Pasta", "pasta.jpg", "video_url", MealType.LUNCH, null, "Italy",
        Difficulty.MEDIUM, "Boil pasta", null);
    when(recipeService.getRecipeById(recipeId)).thenReturn(recipe); // Mocking the recipeService

    // Mock the commentService to return the new comment
    when(commentService.createComment(recipeId, content)).thenReturn(newComment);

    // Act & Assert
    mockMvc.perform(MockMvcRequestBuilders.post("/recipes/{recipeId}/comments", recipeId)
        .param("content", content)
        .session(session))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.view().name("recipe-detail"))
        .andExpect(MockMvcResultMatchers.model().attributeExists("comment"))
        .andExpect(MockMvcResultMatchers.model().attribute("comment", newComment));
  }

  @Test
  void addComment_ShouldRedirectToLogin_WhenNoToken() throws Exception {
    session.clearAttributes();

    mockMvc.perform(MockMvcRequestBuilders.post("/recipes/{recipeId}/comments", 1L)
        .param("content", "Great recipe!")
        .session(session))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
        .andExpect(MockMvcResultMatchers.redirectedUrl("/login"));
  }
}
