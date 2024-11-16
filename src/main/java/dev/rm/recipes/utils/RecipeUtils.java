package dev.rm.recipes.utils;

public class RecipeUtils {

  public static String extractVideoId(String videoUrl) {
    if (videoUrl == null || !videoUrl.contains("v=")) {
      return null;
    }
    String[] urlParts = videoUrl.split("v=");
    if (urlParts.length > 1) {
      return urlParts[1].split("&")[0];
    }
    return null;
  }
}
