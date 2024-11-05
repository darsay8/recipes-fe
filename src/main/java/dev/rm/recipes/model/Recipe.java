package dev.rm.recipes.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {
  private Long id;
  private String name;
  private String image;
  private String mealType;
  private List<Ingredient> ingredients;
  private String countryOfOrigin;
  private String difficulty;
  private String instructions;
}
