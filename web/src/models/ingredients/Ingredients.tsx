import IngredientsDto from "./IngredientsDto";

interface Ingredients {
  toDto(): IngredientsDto;
}

class Ingredients {
  list: Map<string, boolean>;

  constructor(
    list: Map<string, boolean>,
  ) {
    this.list = list;
  }
}

Ingredients.prototype.toDto = function() {
  return new IngredientsDto(
    this.list
  );
}

export default Ingredients;
