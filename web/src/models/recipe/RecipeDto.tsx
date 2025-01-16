import { OBJ_RECIPE_CREATION, OBJ_RECIPE_DESCRIPTION, OBJ_RECIPE_EASINESS, OBJ_RECIPE_HAS_PHOTO, OBJ_RECIPE_INGREDIENTS, OBJ_RECIPE_KEYWORDS, OBJ_RECIPE_NAME, OBJ_RECIPE_TAG_IDS, OBJ_RECIPE_UPDATE, OBJ_RECIPE_URL } from "../../components/Globals";
import { stringToEasiness } from "./Easiness";
import Recipe from "./Recipe";

class RecipeDto {
  id: string;
  name: string;
  keywords: string[];
  creationDate: number;
  updateDate: number;
  easiness: string;
  time: string;
  hasPhoto: boolean;
  recipeUrl: string | null;
  description: string[];
  ingredients: string[];
  tagIds: string[];

  constructor(
    id: string =  "",
    name: string = "",
    keywords: string[] = [],
    creationDate: number = 0,
    updateDate: number = 0,
    easiness: string = "",
    time: string = "",
    hasPhoto: boolean = false,
    recipeUrl: string | null = null,
    description: string[] = [],
    ingredients: string[] = [],
    tagIds: string[] = [],
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

export const recipeDtoToDomain = (dto: RecipeDto) => {
  return new Recipe(
    dto.id,
    dto.name,
    new Date(dto.creationDate),
    new Date(dto.updateDate),
    stringToEasiness(dto.easiness),
    dto.time,
    "",
    dto.recipeUrl,
    dto.description,
    dto.ingredients,
    [],
  );
}

export const recipeDtoToMap = (dto: RecipeDto) => {
  return {
    "id": dto.id,
    [OBJ_RECIPE_NAME]: dto.name,
    [OBJ_RECIPE_KEYWORDS]: dto.keywords,
    [OBJ_RECIPE_CREATION]: new Date(dto.creationDate),
    [OBJ_RECIPE_UPDATE]: new Date(dto.updateDate),
    [OBJ_RECIPE_EASINESS]: stringToEasiness(dto.easiness),
    "time": dto.time,
    [OBJ_RECIPE_HAS_PHOTO]: dto.hasPhoto,
    [OBJ_RECIPE_URL]: dto.recipeUrl,
    [OBJ_RECIPE_DESCRIPTION]: dto.description,
    [OBJ_RECIPE_INGREDIENTS]: dto.ingredients,
    [OBJ_RECIPE_TAG_IDS]: [],
  };
}

export default RecipeDto;
