import { StringFormatters } from "../../components/StringFormatters";
import Tag from "../tag/Tag";
import { Easiness, getEasinessName } from "./Easiness";
import RecipeDto from "./RecipeDto";

interface Recipe {
  toDto(): RecipeDto;
}

class Recipe {
  id: string;
  name: string;
  creationDate: Date;
  updateDate: Date;
  easiness: Easiness;
  time: string;
  photo: URL;
  recipeUrl: string | null;
  description: Array<string>;
  ingredients: Array<string>;
  tags: Array<Tag>;

  constructor(
    id: string,
    name: string,
    creationDate: Date,
    updateDate: Date,
    easiness: Easiness,
    time: string,
    photo: URL = new URL(""),
    recipeUrl: string | null,
    description: Array<string>,
    ingredients: Array<string>,
    tags: Array<Tag>,
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

Recipe.prototype.toDto = function() {
  const keywords: string[] = [];

  this.ingredients.forEach((ingredient) => {
    const keywords = StringFormatters.generateSubStrings(ingredient);
    const each = ingredient.split(' ').map((word) => word.toLowerCase());
    keywords.push(...each);
    keywords.push(...keywords);
  });

  const nameKeywords: string[] = [
    ...this.name.split(' ').map((word) => word.toLowerCase()),
    ...StringFormatters.generateSubStrings(this.name),
  ];

  keywords.push(...nameKeywords);

  return new RecipeDto(
    this.id,
    this.name,
    keywords,
    this.creationDate.getMilliseconds(),
    this.updateDate.getMilliseconds(),
    getEasinessName(this.easiness),
    this.time,
    this.photo != new URL(""),
    this.recipeUrl,
    this.description,
    this.ingredients,
    this.tags.map((e) => e.id),
  );
}

export default Recipe;
