package com.gabr.gabc.qook.presentation.shared

class IntentVars {
    companion object {
        /**
         * Boolean to exit group in PlanningPage if removed
         * */
        const val HAS_REMOVED_SHARED_PLANNING = "HAS_REMOVED_SHARED_PLANNING"

        /**
         * DayPlanning to be passed down to RecipesPage from PlanningPage when adding a recipe to planning
         * */
        const val FROM_PLANNING = "FROM_PLANNING"

        /**
         * Boolean defining if the recipe should be added to Lunch or Dinner
         * */
        const val IS_LUNCH = "IS_LUNCH"

        /**
         * DayPlanning to be passed down to PlanningPage from RecipesPage to update locally
         * */
        const val HAS_UPDATED_DAY_PLANNING = "HAS_UPDATED_PLANNING"

        /**
         * SharedPlanning to be passed down to PlanningSettingsPage from PlanningPage to update locally
         * */
        const val HAS_UPDATED_SHARED_PLANNING = "HAS_UPDATED_SHARED_PLANNING"

        /**
         * String defining the Shared Planning ID
         * */
        const val SHARED_PLANNING_ID = "SHARED_PLANNING_ID"

        /**
         * SharedPlanning to be passed down to PlanningSettingsPage
         * */
        const val SHARED_PLANNING = "SHARED_PLANNING"

        /**
         * User to be passed down to the ProfilePage from Home
         * */
        const val USER = "USER"

        /**
         * Planning to be passed down to the PlanningPage from Home
         * */
        const val PLANNING = "PLANNING"

        /**
         * Boolean to check if the tag overall was altered
         * */
        const val HAS_ALTERED_TAG = "HAS_ALTERED_TAG"

        /**
         * Boolean to check if the Mode was altered
         * */
        const val HAS_ALTERED_MODE = "HAS_ALTERED_MODE"

        /**
         * Tag to be passed when updating it
         * */
        const val UPDATE_TAG = "UPDATE_TAG"

        /**
         * Recipe to be passed down to the list of recipes when added or modified
         * */
        const val RECIPE_UPDATED = "RECIPE_UPDATED"

        /**
         * Recipe to be passed down to RecipesPage from RecipeDetails if modified
         * */
        const val HAS_UPDATED_RECIPE = "HAS_UPDATED_RECIPE"

        /**
         * Removed recipe to be passed down to RecipesPage from RecipeDetails if removed
         * */
        const val HAS_DELETED_RECIPE = "HAS_DELETED_RECIPE"

        /**
         * Recipe to be passed down to AddRecipePage from RecipeDetails to be modified
         * */
        const val RECIPE_FROM_DETAILS = "RECIPE_FROM_DETAILS"

        /**
         * Boolean that defines if the user is able to update a Recipe in RecipeDetails
         * */
        const val ALLOW_TO_UPDATE = "ALLOW_TO_UPDATE"

        /**
         * Recipe to be passed down to RecipeDetails from RecipesList
         * */
        const val RECIPE = "RECIPE"

        /**
         * Recipe Original Poster ID for loading purposes when on shared planning
         * */
        const val RECIPE_OP = "OP"

        /**
         * Recipe to be updated to a DayPlanning defined by [HAS_UPDATED_DAY_PLANNING]
         * */
        const val HAS_UPDATED_DAY_PLANNING_WITH_RECIPE = "HAS_UPDATED_PLANNING_RECIPE"

        /**
         * List of recipes to be passed down to RecipePage from PlanningsPage for local loading
         * */
        const val RECIPES_LIST = "RECIPES_LIST"

        /**
         * User to be modified in HomePage from ProfilePage
         * */
        const val HAS_UPDATED_PROFILE = "HAS_UPDATED_PROFILE"
    }
}