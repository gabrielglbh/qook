import { collection, deleteDoc, doc, getDoc, getDocs, limit, query, setDoc, where } from "firebase/firestore";
import Recipe from "../../models/recipe/Recipe";
import { auth, db, storage } from "../firebase.config";
import { DB_RECIPES, DB_USER, FIREBASE_HOST, OBJ_RECIPE_KEYWORDS, OBJ_RECIPE_NAME, OBJ_RECIPE_TAG_IDS, RECIPES_LIMIT, STORAGE_RECIPES, STORAGE_USERS } from "../../components/Globals";
import { deleteObject, getDownloadURL, ref, uploadBytes } from "firebase/storage";
import RecipeDto from "../../models/recipe/RecipeDto";
import { getTagsForRecipe } from "./TagService";

export const createRecipe = async (recipe: Recipe): Promise<Recipe | string> => {
    const currentUser = auth.currentUser; 
    const recipesCollection = collection(db, DB_USER, currentUser!.uid, DB_RECIPES);
    const q = query(recipesCollection, where(OBJ_RECIPE_NAME, "==", recipe.name), limit(1));
    const querySnapshot = await getDocs(q);

    if (!querySnapshot.empty) {
      return "Receta duplicada";
    }

    const docRef = doc(recipesCollection);
    const recipeId = docRef.id;

    const uploadedRecipe = { ...recipe, id: recipeId };
    await setDoc(docRef, uploadedRecipe);

    if (recipe.photo !== "" && recipe.photo !== FIREBASE_HOST) {
      const storageRef = ref(storage, `${STORAGE_USERS}${currentUser!.uid}/${STORAGE_RECIPES}${recipeId}.jpg`);
      const photoBlob = await fetch(recipe.photo).then((response) => response.blob());
      await uploadBytes(storageRef, photoBlob);

      const photoUrl = await getDownloadURL(storageRef);
      uploadedRecipe.photo = photoUrl;
    }

    return { ...uploadedRecipe, id: recipeId }
}

export const getRecipes = async (
    orderBy: string,
    queryText: string | null,
    tagId: string | null,
    lastRecipeId: string
): Promise<Recipe[]> => {
    try {
        const uid = auth.currentUser!.uid; 
        const recipesCollection = collection(db, DB_USER, uid, DB_RECIPES);

        let q = query(recipesCollection);

        if (tagId) {
            q = query(q, where(OBJ_RECIPE_TAG_IDS, "array-contains", tagId));
        } else if (queryText?.trim()) {
            q = query(q, where(OBJ_RECIPE_KEYWORDS, "array-contains", queryText.toLowerCase()));
        }

        if (lastRecipeId) {
            const lastDocSnapshot = doc(recipesCollection, lastRecipeId);
            // TODO: q = startAfter(lastDocSnapshot);
        }

        const querySnapshot = await getDocs(query(q, limit(RECIPES_LIMIT)));

        const recipes: Recipe[] = [];
        querySnapshot.forEach(async (doc) => {
        const recipeDto = doc.data() as RecipeDto;
        if (recipeDto) {
            const recipe = { ...recipeDto, id: doc.id }.toDomain();

            if (recipeDto.hasPhoto) {
            const photoRef = await getDownloadURL(
                ref(storage, `${STORAGE_USERS}${uid}/${STORAGE_RECIPES}${recipeDto.id}.jpg`)
            );
            recipe.photo = photoRef;
            }

            const tags = await getTagsForRecipe(recipeDto, uid);
            recipe.tags = tags;

            recipes.push(recipe);
        }
        });

        return recipes
    } catch (_) {
        return []
    }
}

export const updateRecipe = async (recipe: Recipe): Promise<Recipe | string> => {
    try {
        const docRef = doc(collection(db, DB_USER, auth.currentUser?.uid!, DB_RECIPES), recipe.id);

        await setDoc(docRef, recipe.toDto().toMap()); 

        const recipePhotoPath = `${STORAGE_USERS}${auth.currentUser?.uid}/${STORAGE_RECIPES}${recipe.id}.jpg`;

        if (recipe.photo.includes(FIREBASE_HOST) && recipe.photo !== "") {
            const photoBlob = await fetch(recipe.photo).then((response) => response.blob()); 
            await uploadBytes(ref(storage, recipePhotoPath), photoBlob);
        } else if (recipe.photo === "") {
            await deleteObject(ref(storage, recipePhotoPath));
        }

        return recipe
  } catch (error) {
        return "No se ha podido actualizar la receta"
  }
}

export const removeRecipe = async (recipeId: string): Promise<void | string> => {
    try {
        const docRef = doc(collection(db, DB_USER, auth.currentUser?.uid!, DB_RECIPES), recipeId);
        await deleteDoc(docRef);

        const recipePhotoPath = `${STORAGE_USERS}${auth.currentUser?.uid}/${STORAGE_RECIPES}${recipeId}.jpg`;
        await deleteObject(ref(storage, recipePhotoPath));

        return
    } catch (error) {
        return "No se ha podido eliminar la receta"
    }
}

export const getRecipeFromUser = async (recipeId: string, userId: string): Promise<Recipe | string> => {
    try {
        const docRef = doc(db, DB_USER, userId, DB_RECIPES, recipeId);
        const docSnapshot = await getDoc(docRef);

        if (!docSnapshot.exists()) {
            return "La receta no se ha encontrado"
        }

        const recipeDto = docSnapshot.data() as RecipeDto;

        const recipe = {
            ...recipeDto.toDomain(),
            id: recipeId,
        };

        if (recipeDto.hasPhoto) {
            const photoRef = ref(storage, `${STORAGE_USERS}${userId}/${STORAGE_RECIPES}${recipeId}.jpg`);
            const photoUrl = await getDownloadURL(photoRef);
            recipe.photo = photoUrl;
        }

        const tags = await getTagsForRecipe(recipeDto, userId);
        recipe.tags = tags;

        return recipe
    } catch (error) {
        return "No se ha podido cargar la receta"
    }
}
