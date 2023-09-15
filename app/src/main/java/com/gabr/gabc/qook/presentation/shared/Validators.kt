package com.gabr.gabc.qook.presentation.shared

import android.util.Patterns
import com.gabr.gabc.qook.domain.recipe.Recipe

class Validators {
    companion object {
        private const val RESTRICTED_CHARACTERS = """[~*/\[\]]"""

        private fun hasRestrictedCharacter(value: String): Boolean =
            Regex(RESTRICTED_CHARACTERS).containsMatchIn(value)

        fun isEmailInvalid(email: String) = !Patterns.EMAIL_ADDRESS.matcher(email).matches()
        fun isPasswordInvalid(password: String) = password.trim().isEmpty() || password.length < 6
        fun isNameInvalid(name: String) =
            name.trim().isEmpty() || name.length > 24 || hasRestrictedCharacter(name)

        fun isRecipeNameInvalid(name: String) =
            name.trim().isEmpty() || name.length > 64 || hasRestrictedCharacter(name)

        fun isDescriptionInvalid(description: String) =
            description.trim().isEmpty() || hasRestrictedCharacter(description)

        fun isIngredientNameInvalid(name: String) =
            name.trim().isEmpty() || name.length > 48 || hasRestrictedCharacter(name)

        fun isRecipeInvalid(recipe: Recipe) =
            recipe == Recipe.EMPTY || recipe.tags.isEmpty() || recipe.ingredients.isEmpty() ||
                    (recipe.description.isEmpty() && recipe.recipeUrl == null) || isRecipeNameInvalid(
                recipe.name
            ) ||
                    isNameInvalid(recipe.time)
    }
}