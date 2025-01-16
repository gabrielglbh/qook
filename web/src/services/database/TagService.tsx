import { deleteDoc, doc, setDoc, where, query, collection, getDocs, updateDoc, orderBy, getDoc } from "firebase/firestore";
import Tag, { tagToDto } from "../../models/tag/Tag";
import { auth, db } from "../firebase.config";
import { DB_RECIPES, DB_TAGS, DB_USER, OBJ_RECIPE_TAG_IDS, OBJ_TAG_NAME } from "../../components/Globals";
import RecipeDto from "../../models/recipe/RecipeDto";
import TagDto, { tagDtoToDomain, tagDtoToMap } from "../../models/tag/TagDto";

export const createTag = async (tag: Tag): Promise<Tag | string> => {
    const currentUser = auth.currentUser; 
    const ref = doc(db, DB_USER, currentUser!.uid, DB_TAGS);
    const tagWithId = {...tag, id: ref.id};
    return await setDoc(ref, tagDtoToMap(tagToDto(tagWithId)))
        .then((_) => {
            return tagWithId
        })
        .catch((error) => {
            return error.message
        })
}

export const removeTag = async (id: string): Promise<void | string> => {
    const currentUser = auth.currentUser; 
    const ref = doc(db, DB_USER, currentUser!.uid, DB_TAGS, id);
    await deleteDoc(ref)
    const recipeQuery = query(
        collection(db, DB_USER, currentUser!.uid, DB_RECIPES),
        where(OBJ_RECIPE_TAG_IDS, "array-contains", id)
    )
    const data = (await getDocs(recipeQuery)).docs
    data.forEach(async (doc) => {
        const recipe = doc.data() as RecipeDto;
        const newTags = recipe.tagIds.filter((tagId) => tagId !== id)
        await updateDoc(doc.ref, {
            [OBJ_RECIPE_TAG_IDS]: newTags,
        });
    });
}

export const updateTag = async (tag: Tag): Promise<void | string> => {
    const currentUser = auth.currentUser; 
    const ref = doc(db, DB_USER, currentUser!.uid, DB_TAGS, tag.id);
    return await updateDoc(ref, tagDtoToMap(tagToDto(tag)))
        .then((_) => {
            return
        })
        .catch((error) => {
            return error.message
        })
}

export const getTags = async (): Promise<Tag[] | string> => {
    const currentUser = auth.currentUser; 
    const q = query(
        collection(db, DB_USER, currentUser!.uid, DB_TAGS),
        orderBy(OBJ_TAG_NAME)
    )
    return await getDocs(q)
        .then((res) => {
            return res.docs.map((d) => {
                const dto = d.data() as TagDto
                return tagDtoToDomain(dto)
            })
        })
        .catch((error) => {
            return error.message
        })
}

export const getTagsForRecipe = async (recipeDto: RecipeDto, userId: string): Promise<Tag[]> => {
    const tags: Tag[] = []
    
    recipeDto.tagIds.forEach(async (id) => {
        const tag = await getDoc(doc(db, DB_USER, userId, DB_TAGS, id))
        tags.push(tagDtoToDomain((tag.data() as TagDto)))
    })
    
    return tags
}
