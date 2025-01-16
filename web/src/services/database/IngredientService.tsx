import { deleteField, doc, FieldValue, getDoc, updateDoc } from "firebase/firestore";
import { auth, db } from "../firebase.config";
import { DB_INGREDIENTS, DB_SHOPPING_LIST, DB_USER, OBJ_SHOPPING_LIST } from "../../components/Globals";
import IngredientsDto from "../../models/ingredients/IngredientsDto";
import Ingredients from "../../models/ingredients/Ingredients";

export const getIngredientsOfShoppingList = async (): Promise<Ingredients> => {
    const currentUser = auth.currentUser; 
    const ref = doc(db, DB_USER, currentUser!.uid, DB_SHOPPING_LIST, DB_INGREDIENTS);
    return await getDoc(ref)
        .then((res) => {
            const data = res.data() as IngredientsDto;
            return data.toDomain();
        })
        .catch((_) => {
            return new Ingredients(new Map());
        });
}

export const removeIngredient = async (ingredient: Ingredients) => {
    const currentUser = auth.currentUser; 
    const ref = doc(db, DB_USER, currentUser!.uid, DB_SHOPPING_LIST, DB_INGREDIENTS);
    var mappedIngredients: Map<string, FieldValue> = new Map();
    ingredient.list.forEach((_, key) => {
        const fieldPath = `${OBJ_SHOPPING_LIST}.${key}`; 
        mappedIngredients.set(fieldPath, deleteField());
    });
    await updateDoc(ref, Object.fromEntries(mappedIngredients));
}

export const updateIngredients = async (ingredient: Ingredients) => {
    const currentUser = auth.currentUser; 
    const ref = doc(db, DB_USER, currentUser!.uid, DB_SHOPPING_LIST, DB_INGREDIENTS);
    await updateDoc(ref, ingredient.toDto().toMap())
}

export const resetIngredients = async () => {
    const currentUser = auth.currentUser; 
    const ref = doc(db, DB_USER, currentUser!.uid, DB_SHOPPING_LIST, DB_INGREDIENTS);
    await updateDoc(ref, new Ingredients(new Map()).toDto().toMap())
}
