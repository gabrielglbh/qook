import { OBJ_RECIPE_CREATION, OBJ_RECIPE_DESCRIPTION, OBJ_RECIPE_EASINESS, OBJ_RECIPE_HAS_PHOTO, OBJ_RECIPE_INGREDIENTS, OBJ_RECIPE_KEYWORDS, OBJ_RECIPE_NAME, OBJ_RECIPE_TAG_IDS, OBJ_RECIPE_UPDATE, OBJ_RECIPE_URL } from "../../components/Globals";
import { stringToEasiness } from "./Easiness";
import Recipe from "./Recipe";

interface RecipeDto {
  toDomain(): Recipe;
  toMap(): {};
}

class RecipeDto {
  id: string;
  name: string;
  keywords: Array<string>;
  creationDate: number;
  updateDate: number;
  easiness: string;
  time: string;
  hasPhoto: boolean;
  recipeUrl: string | null;
  description: Array<string>;
  ingredients: Array<string>;
  tagIds: Array<string>;

  constructor(
    id: string =  "",
    name: string = "",
    keywords: Array<string> = [],
    creationDate: number = 0,
    updateDate: number = 0,
    easiness: string = "",
    time: string = "",
    hasPhoto: boolean = false,
    recipeUrl: string | null = null,
    description: Array<string> = [],
    ingredients: Array<string> = [],
    tagIds: Array<string> = [],
  ) {
    this.id = id;
    this.name = name;
    this.keywords = keywords;
    this.creationDate = creationDate;
    this.updateDate = updateDate;
    this.easiness = easiness;
    this.time = time;
    this.hasPhoto = hasPhoto;
    this.recipeUrl = recipeUrl;
    this.description = description;
    this.ingredients = ingredients;
    this.tagIds = tagIds;
  }
}

RecipeDto.prototype.toDomain = function() {
  return new Recipe(
    this.id,
    this.name,
    new Date(this.creationDate),
    new Date(this.updateDate),
    stringToEasiness(this.easiness),
    this.time,
    new URL(""),
    this.recipeUrl,
    this.description,
    this.ingredients,
    [],
  );
}

RecipeDto.prototype.toMap = function() {
  return {
    "id": this.id,
    [OBJ_RECIPE_NAME]: this.name,
    [OBJ_RECIPE_KEYWORDS]: this.keywords,
    [OBJ_RECIPE_CREATION]: new Date(this.creationDate),
    [OBJ_RECIPE_UPDATE]: new Date(this.updateDate),
    [OBJ_RECIPE_EASINESS]: stringToEasiness(this.easiness),
    "time": this.time,
    [OBJ_RECIPE_HAS_PHOTO]: this.hasPhoto,
    [OBJ_RECIPE_URL]: this.recipeUrl,
    [OBJ_RECIPE_DESCRIPTION]: this.description,
    [OBJ_RECIPE_INGREDIENTS]: this.ingredients,
    [OBJ_RECIPE_TAG_IDS]: [],
  };
}

export default RecipeDto;
