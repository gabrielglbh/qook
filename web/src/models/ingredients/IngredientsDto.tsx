import { OBJ_SHOPPING_LIST } from "../../components/Globals";
import Ingredients from "./Ingredients";

interface IngredientsDto {
  toDomain(): Ingredients;
  toMap(): {};
}

class IngredientsDto {
  list: Map<string, boolean>;

  constructor(
    list: Map<string, boolean>,
  ) {
    this.list = list;
  }
}

IngredientsDto.prototype.toDomain = function() {
  return new Ingredients(
    this.list
  );
}

IngredientsDto.prototype.toMap = function() {
  return {
    [OBJ_SHOPPING_LIST]: this.list,
  }
}

export default IngredientsDto;
