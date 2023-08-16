package com.gabr.gabc.qook.presentation.shared

import android.util.Patterns
import com.gabr.gabc.qook.domain.recipe.Recipe

class Validators {
    companion object {
        fun isEmailInvalid(email: String) = !Patterns.EMAIL_ADDRESS.matcher(email).matches()
        fun isPasswordInvalid(password: String) = password.trim().isEmpty() || password.length < 6
        fun isNameInvalid(name: String) = name.trim().isEmpty() || name.length > 24
        fun isRecipeNameInvalid(name: String) = name.trim().isEmpty() || name.length > 64
        fun isRecipeInvalid(recipe: Recipe) =
            recipe == Recipe.EMPTY_RECIPE || recipe.tags.isEmpty() || recipe.ingredients.isEmpty() ||
                    recipe.description.trim().isEmpty() || isRecipeNameInvalid(recipe.name) ||
                    isNameInvalid(recipe.time)
    }
}