package dev.rm.recipes.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class RecipeUtilsTest {

  @Test
  public void testExtractVideoId_ValidUrl() {
    // Arrange
    String videoUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";

    // Act
    String result = RecipeUtils.extractVideoId(videoUrl);

    // Assert
    assertEquals("dQw4w9WgXcQ", result, "The video ID should be extracted correctly.");
  }

  @Test
  public void testExtractVideoId_ValidUrlWithAdditionalParams() {
    // Arrange
    String videoUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ&feature=share";

    // Act
    String result = RecipeUtils.extractVideoId(videoUrl);

    // Assert
    assertEquals("dQw4w9WgXcQ", result, "The video ID should be extracted, ignoring other parameters.");
  }

  @Test
  public void testExtractVideoId_UrlWithoutVideoId() {
    // Arrange
    String videoUrl = "https://www.youtube.com/watch?feature=share";

    // Act
    String result = RecipeUtils.extractVideoId(videoUrl);

    // Assert
    assertNull(result, "The video ID should be null when 'v=' is not present.");
  }

  @Test
  public void testExtractVideoId_NullUrl() {
    // Arrange
    String videoUrl = null;

    // Act
    String result = RecipeUtils.extractVideoId(videoUrl);

    // Assert
    assertNull(result, "The video ID should be null when the URL is null.");
  }

  @Test
  public void testExtractVideoId_EmptyUrl() {
    // Arrange
    String videoUrl = "";

    // Act
    String result = RecipeUtils.extractVideoId(videoUrl);

    // Assert
    assertNull(result, "The video ID should be null when the URL is empty.");
  }

  @Test
  public void testExtractVideoId_MalformedUrl() {
    // Arrange
    String videoUrl = "https://www.youtube.com/watch?abc=123";

    // Act
    String result = RecipeUtils.extractVideoId(videoUrl);

    // Assert
    assertNull(result, "The video ID should be null when 'v=' is not in the URL.");
  }

  @Test
  public void testExtractVideoId_UrlWithMultipleParameters() {
    // Arrange
    String videoUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ&xyz=123";

    // Act
    String result = RecipeUtils.extractVideoId(videoUrl);

    // Assert
    assertEquals("dQw4w9WgXcQ", result,
        "The video ID should be correctly extracted from a URL with multiple parameters.");
  }
}
