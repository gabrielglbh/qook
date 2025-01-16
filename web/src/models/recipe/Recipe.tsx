import { StringFormatters } from "../../components/StringFormatters";
import Tag from "../tag/Tag";
import { Easiness, getEasinessName } from "./Easiness";
import RecipeDto from "./RecipeDto";

class Recipe {
  id: string;
  name: string;
  creationDate: Date;
  updateDate: Date;
  easiness: Easiness;
  time: string;
  photo: string;
  recipeUrl: string | null;
  description: string[];
  ingredients: string[];
  tags: Tag[];

  constructor(
    id: string,
    name: string,
    creationDate: Date,
    updateDate: Date,
    easiness: Easiness,
    time: string,
    photo: string = "",
    recipeUrl: string | null,
    description: string[],
    ingredients: string[],
    tags: Tag[],
  ) {
    this.id = id;
    this.name = name;
    this.creationDate = creationDate;
    this.updateDate = updateDate;
    this.easiness = easiness;
    this.time = time;
    this.photo = photo;
    this.recipeUrl = recipeUrl;
    this.description = description;
    this.ingredients = ingredients;
    this.tags = tags;
  }
}

export const recipeToDto = (recipe: Recipe) => {
  const keywords: string[] = [];

  recipe.ingredients.forEach((ingredient) => {
    const keywords = StringFormatters.generateSubStrings(ingredient);
    const each = ingredient.split(' ').map((word) => word.toLowerCase());
    keywords.push(...each);
    keywords.push(...keywords);
  });

  const nameKeywords: string[] = [
    ...recipe.name.split(' ').map((word) => word.toLowerCase()),
    ...StringFormatters.generateSubStrings(recipe.name),
  ];

  keywords.push(...nameKeywords);

  return new RecipeDto(
    recipe.id,
    recipe.name,
    keywords,
    recipe.creationDate.getMilliseconds(),
    recipe.updateDate.getMilliseconds(),
    getEasinessName(recipe.easiness),
    recipe.time,
    recipe.photo != "",
    recipe.recipeUrl,
    recipe.description,
    recipe.ingredients,
    recipe.tags.map((e) => e.id),
  );
}

export default Recipe;
