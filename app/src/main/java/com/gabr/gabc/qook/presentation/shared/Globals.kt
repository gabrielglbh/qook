package com.gabr.gabc.qook.presentation.shared

class Globals {
    companion object {
        const val FIREBASE_HOST = "firebasestorage.googleapis.com"

        const val DB_USER = "USERS"
        const val DB_RECIPES = "RECIPES"
        const val DB_TAGS = "TAGS"
        const val DB_PLANNING = "PLANNING"
        const val DB_SHOPPING_LIST = "SHOPPING_LIST"
        const val DB_INGREDIENTS = "INGREDIENTS"

        const val STORAGE_RECIPES = "recipes/"
        const val STORAGE_AVATAR = "avatar/photo.jpg"

        const val OBJ_RECIPE_TAG_IDS = "tagIds"
        const val OBJ_RECIPE_NAME = "name"
        const val OBJ_RECIPE_KEYWORDS = "keywords"
        const val OBJ_RECIPE_EASINESS = "easiness"
        const val OBJ_RECIPE_CREATION = "creationDate"
        const val OBJ_RECIPE_UPDATE = "updateDate"
        const val OBJ_RECIPE_URL = "recipeUrl"
        const val OBJ_RECIPE_DESCRIPTION = "description"
        const val OBJ_RECIPE_INGREDIENTS = "ingredients"
        const val OBJ_RECIPE_HAS_PHOTO = "hasPhoto"

        const val OBJ_TAG_NAME = "text"
        const val OBJ_TAG_KEYWORDS = "keywords"

        const val OBJ_PLANNING_FIRST_DAY = "firstDay"
        const val OBJ_PLANNING_SECOND_DAY = "secondDay"
        const val OBJ_PLANNING_THIRD_DAY = "thirdDay"
        const val OBJ_PLANNING_FOURTH_DAY = "fourthDay"
        const val OBJ_PLANNING_FIFTH_DAY = "fifthDay"
        const val OBJ_PLANNING_SIXTH_DAY = "sixthDay"
        const val OBJ_PLANNING_SEVENTH_DAY = "seventhDay"
        const val OBJ_PLANNING_DAY_INDEX = "dayIndex"

        const val OBJ_SHOPPING_LIST = "list"
    }
}