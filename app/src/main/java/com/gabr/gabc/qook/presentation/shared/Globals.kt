package com.gabr.gabc.qook.presentation.shared

class Globals {
    companion object {
        const val FIREBASE_HOST = "firebasestorage.googleapis.com"

        const val DB_USER = "USERS"
        const val DB_RECIPES = "RECIPES"
        const val DB_TAGS = "TAGS"

        const val STORAGE_RECIPES = "recipes/"
        const val STORAGE_AVATAR = "avatar/photo.jpg"

        const val OBJ_RECIPE_TAG_IDS = "tagIds"
        const val OBJ_RECIPE_NAME = "name"
        const val OBJ_RECIPE_KEYWORDS = "keywords"
        const val OBJ_RECIPE_EASINESS = "easiness"
        const val OBJ_RECIPE_CREATION = "creationDate"
        const val OBJ_RECIPE_UPDATE = "updateDate"
        const val OBJ_RECIPE_DESCRIPTION = "description"
        const val OBJ_RECIPE_INGREDIENTS = "ingredients"
        const val OBJ_RECIPE_HAS_PHOTO = "hasPhoto"

        const val OBJ_TAG_NAME = "text"
        const val OBJ_TAG_KEYWORDS = "keywords"
    }
}