import { OBJ_SHOPPING_LIST } from "../../components/Globals";
import Ingredients from "./Ingredients";

class IngredientsDto {
  list: Map<string, boolean>;

  constructor(
    list: Map<string, boolean>,
  ) {
    this.list = list;
  }
}

export const ingredientsDtoToDomain = (dto: IngredientsDto) => {
  return new Ingredients(
    dto.list
  );
}

export const ingredientsDtoToMap = (dto: IngredientsDto) => {
  return {
    [OBJ_SHOPPING_LIST]: dto.list,
  }
}

export default IngredientsDto;
