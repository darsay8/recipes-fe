package dev.rm.recipes.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import dev.rm.recipes.model.Comment;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class CommentServiceTests {

  @Mock
  private RestTemplate restTemplate;

  @InjectMocks
  private CommentService commentService;

  private static final String BASE_URL = "http://localhost:8092";

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    commentService.setBackendUrl(BASE_URL);
  }

  // @Test
  // void getCommentsByRecipeId_ShouldReturnComments() {
  // Long recipeId = 1L;
  // Comment[] commentsArray = new Comment[] {
  // new Comment("Great recipe!", "user1"),
  // new Comment("Easy to make!", "user2")
  // };

  // ResponseEntity<Comment[]> responseEntity = ResponseEntity.ok(commentsArray);
  // when(restTemplate.exchange(eq(BASE_URL + "/recipes/" + recipeId +
  // "/comments"),
  // eq(HttpMethod.GET), isNull(), eq(Comment[].class)))
  // .thenReturn(responseEntity);

  // List<Comment> comments = commentService.getCommentsByRecipeId(recipeId);

  // assertNotNull(comments);
  // assertEquals(2, comments.size());
  // assertEquals("Great recipe!", comments.get(0).getContent());
  // }

  // @Test
  // void createComment_ShouldReturnComment_WhenSuccess() {
  // Long recipeId = 1L;
  // String content = "Looks delicious!";
  // Comment comment = new Comment(content, "user1");
  // HttpEntity<Comment> request = new HttpEntity<>(comment);

  // ResponseEntity<Comment> responseEntity =
  // ResponseEntity.status(HttpStatus.CREATED).body(comment);

  // when(restTemplate.exchange(eq(BASE_URL + "/recipes/" + recipeId +
  // "/comments"), eq(HttpMethod.POST), eq(request),
  // eq(Comment.class)))
  // .thenReturn(responseEntity);

  // Comment createdComment = commentService.createComment(recipeId, content);

  // assertNotNull(createdComment);
  // assertEquals(content, createdComment.getContent());
  // }

  @Test
  void createComment_ShouldHandleError_WhenFailed() {
    Long recipeId = 1L;
    String content = "Bad content!";
    Comment comment = new Comment(content, "user1");
    HttpEntity<Comment> request = new HttpEntity<>(comment);
    ResponseEntity<Comment> responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    when(restTemplate.exchange(eq(BASE_URL + "/recipes/" + recipeId + "/comments"), eq(HttpMethod.POST), eq(request),
        eq(Comment.class)))
        .thenReturn(responseEntity);

    assertThrows(RuntimeException.class, () -> commentService.createComment(recipeId, content));
  }

}
