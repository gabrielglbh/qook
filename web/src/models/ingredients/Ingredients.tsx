import IngredientsDto from "./IngredientsDto";

class Ingredients {
  list: Map<string, boolean>;

  constructor(
    list: Map<string, boolean>,
  ) {
    this.list = list;
  }
}

export const ingredientsToDto = (ingredients: Ingredients) => {
  return new IngredientsDto(ingredients.list);
}

export default Ingredients;
