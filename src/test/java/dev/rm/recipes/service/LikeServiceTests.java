package dev.rm.recipes.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import dev.rm.recipes.model.Like;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

public class LikeServiceTests {

  @Mock
  private RestTemplate restTemplate;

  @InjectMocks
  private LikeService likeService;

  private static final String BASE_URL = "http://localhost:8092";

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    likeService.setBackendUrl(BASE_URL);
  }

  @Test
  void getLikesCountByRecipeId_ShouldReturnLikesCount() {
    // Arrange
    Long recipeId = 1L;
    long expectedLikes = 10L;
    ResponseEntity<Long> responseEntity = ResponseEntity.ok(expectedLikes);
    when(restTemplate.exchange(eq(BASE_URL + "/recipes/1/likes/total"), eq(HttpMethod.GET), isNull(), eq(Long.class)))
        .thenReturn(responseEntity);

    // Act
    long likesCount = likeService.getLikesCountByRecipeId(recipeId);

    // Assert
    assertEquals(expectedLikes, likesCount);
  }

  @Test
  void getLikesCountByRecipeId_ShouldReturnLikesCount_WhenApiReturnsValidResponse() {
    // Arrange
    Long recipeId = 1L;
    Long likesCount = 42L;

    when(restTemplate.exchange(
        eq(BASE_URL + "/recipes/" + recipeId + "/likes/total"),
        eq(HttpMethod.GET),
        isNull(),
        eq(Long.class)))
        .thenReturn(new ResponseEntity<>(likesCount, HttpStatus.OK));

    // Act
    long result = likeService.getLikesCountByRecipeId(recipeId);

    // Assert
    assertEquals(likesCount, result);
    verify(restTemplate, times(1)).exchange(
        eq(BASE_URL + "/recipes/" + recipeId + "/likes/total"),
        eq(HttpMethod.GET),
        isNull(),
        eq(Long.class));
  }

  @Test
  void getLikesCountByRecipeId_ShouldReturnZero_WhenApiReturnsNull() {
    // Arrange
    Long recipeId = 1L;

    when(restTemplate.exchange(
        eq(BASE_URL + "/recipes/" + recipeId + "/likes/total"),
        eq(HttpMethod.GET),
        isNull(),
        eq(Long.class)))
        .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

    // Act
    long result = likeService.getLikesCountByRecipeId(recipeId);

    // Assert
    assertEquals(0L, result);
    verify(restTemplate, times(1)).exchange(
        eq(BASE_URL + "/recipes/" + recipeId + "/likes/total"),
        eq(HttpMethod.GET),
        isNull(),
        eq(Long.class));
  }

  @Test
  void createLike_ShouldReturnLike_WhenApiCallSucceeds() {
    // Arrange
    Long recipeId = 1L;
    Like like = new Like();
    Like createdLike = new Like();

    when(restTemplate.exchange(
        eq(BASE_URL + "/recipes/" + recipeId + "/likes"),
        eq(HttpMethod.POST),
        any(HttpEntity.class),
        eq(Like.class)))
        .thenReturn(new ResponseEntity<>(createdLike, HttpStatus.CREATED));

    // Act
    Like result = likeService.createLike(recipeId);

    // Assert
    assertNotNull(result);
    assertEquals(createdLike, result);
    verify(restTemplate, times(1)).exchange(
        eq(BASE_URL + "/recipes/" + recipeId + "/likes"),
        eq(HttpMethod.POST),
        any(HttpEntity.class),
        eq(Like.class));
  }

  @Test
  void createLike_ShouldThrowRuntimeException_WhenApiCallFails() {
    // Arrange
    Long recipeId = 1L;
    Like like = new Like();

    when(restTemplate.exchange(
        eq(BASE_URL + "/recipes/" + recipeId + "/likes"),
        eq(HttpMethod.POST),
        any(HttpEntity.class),
        eq(Like.class)))
        .thenReturn(new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR));

    // Act & Assert
    RuntimeException exception = assertThrows(RuntimeException.class, () -> likeService.createLike(recipeId));
    assertEquals("Failed to create like", exception.getMessage());
    verify(restTemplate, times(1)).exchange(
        eq(BASE_URL + "/recipes/" + recipeId + "/likes"),
        eq(HttpMethod.POST),
        any(HttpEntity.class),
        eq(Like.class));
  }

  @Test
  void testGetLikesCountByRecipeId_Success() {
    Long recipeId = 1L;
    Long expectedLikesCount = 10L;

    // Mock the HTTP response from the RestTemplate
    ResponseEntity<Long> mockResponse = new ResponseEntity<>(expectedLikesCount, HttpStatus.OK);
    when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Long.class)))
        .thenReturn(mockResponse);

    // Call the method and assert the result
    long result = likeService.getLikesCountByRecipeId(recipeId);
    assertEquals(expectedLikesCount, result);
  }

  @Test
  void testGetLikesCountByRecipeId_NotFound() {
    Long recipeId = 2L;

    // Mock the HTTP response with null body
    ResponseEntity<Long> mockResponse = new ResponseEntity<>(null, HttpStatus.OK);
    when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Long.class)))
        .thenReturn(mockResponse);

    // Call the method and assert the result
    long result = likeService.getLikesCountByRecipeId(recipeId);
    assertEquals(0L, result);
  }

  @Test
  void testCreateLike_Success() {
    Long recipeId = 1L;
    Like mockLike = new Like();
    mockLike.setLikeId(123L);

    // Mock the HTTP response with status CREATED
    ResponseEntity<Like> mockResponse = new ResponseEntity<>(mockLike, HttpStatus.CREATED);
    when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Like.class)))
        .thenReturn(mockResponse);

    // Call the method and assert the result
    Like result = likeService.createLike(recipeId);
    assertNotNull(result);
    assertEquals(mockLike.getLikeId(), result.getLikeId());
  }

  @Test
  void testCreateLike_Failure() {
    Long recipeId = 2L;

    // Mock the HTTP response with a failure status (e.g., BAD_REQUEST)
    ResponseEntity<Like> mockResponse = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Like.class)))
        .thenReturn(mockResponse);

    // Call the method and assert that an exception is thrown
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      likeService.createLike(recipeId);
    });

    assertEquals("Failed to create like", exception.getMessage());
  }

}
