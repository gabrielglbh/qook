package com.gabr.gabc.qook.presentation.shared.components

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ClearAll
import androidx.compose.material.icons.outlined.KeyboardOptionKey
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.domain.planning.DayPlanning
import com.gabr.gabc.qook.domain.recipe.Recipe
import com.gabr.gabc.qook.presentation.shared.QDateUtils

@Composable
fun QPlanning(
    planning: List<DayPlanning>,
    modifier: Modifier = Modifier,
    onClearFullDayPlanning: (DayPlanning) -> Unit,
    onAddRecipeToDayPlanning: (DayPlanning, Boolean) -> Unit,
    onRecipeTapped: (Recipe) -> Unit,
    onClearDayPlanning: (DayPlanning, Boolean) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        planning.forEach {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 24.dp),
                ) {
                    Text(
                        stringResource(QDateUtils.getWeekDayStringRes(it.dayIndex)),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        onClearFullDayPlanning(it)
                    }) {
                        Icon(Icons.Outlined.ClearAll, "")
                    }
                }
                Spacer(modifier = Modifier.size(8.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(end = 12.dp)
                ) {
                    PlanningDayRecipe(
                        it,
                        it.lunch.meal,
                        true,
                        onAddRecipeToDayPlanning,
                        onRecipeTapped,
                        onClearDayPlanning,
                    )
                    PlanningDayRecipe(
                        it,
                        it.dinner.meal,
                        false,
                        onAddRecipeToDayPlanning,
                        onRecipeTapped,
                        onClearDayPlanning,
                    )
                }
            }
        }
    }
}

@Composable
fun PlanningDayRecipe(
    dayPlanning: DayPlanning,
    recipe: Recipe,
    isLunch: Boolean,
    onAddRecipeToDayPlanning: (DayPlanning, Boolean) -> Unit,
    onRecipeTapped: (Recipe) -> Unit,
    onClearDayPlanning: (DayPlanning, Boolean) -> Unit,
) {
    var recipeForOptions by remember { mutableStateOf(Recipe.EMPTY_RECIPE) }

    if (recipeForOptions != Recipe.EMPTY_RECIPE) {
        QDialog(
            onDismissRequest = { recipeForOptions = Recipe.EMPTY_RECIPE },
            leadingIcon = Icons.Outlined.KeyboardOptionKey,
            title = R.string.plannings_options_title,
            content = {
                Text(stringResource(R.string.plannings_options_description, recipeForOptions.name))
            },
            buttonTitle = R.string.plannings_replace,
            onSubmit = {
                onAddRecipeToDayPlanning(dayPlanning, isLunch)
                recipeForOptions = Recipe.EMPTY_RECIPE
            },
            buttonSecondaryTitle = R.string.plannings_remove,
            onSubmitSecondary = {
                onClearDayPlanning(dayPlanning, isLunch)
                recipeForOptions = Recipe.EMPTY_RECIPE
            },
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        QAutoSizeText(
            if (isLunch) {
                stringResource(R.string.planning_lunch)
            } else {
                stringResource(R.string.planning_dinner)
            },
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .rotate(-90f)
                .width(56.dp),
        )
        if (recipe == Recipe.EMPTY_RECIPE) QAutoSizeText(
            stringResource(R.string.planning_no_recipe_added),
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.outline,
                fontWeight = FontWeight.Normal,
            ),
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp)
        )
        if (recipe == Recipe.EMPTY_RECIPE) {
            QImageContainer(
                uri = Uri.EMPTY,
                placeholder = Icons.Outlined.Add,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.padding(8.dp),
                size = 72.dp,
            ) {
                onAddRecipeToDayPlanning(dayPlanning, isLunch)
            }
        } else {
            QRecipeItem(
                recipe = recipe,
                simplified = true,
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                onClick = {
                    onRecipeTapped(recipe)
                },
                onLongClick = {
                    recipeForOptions = if (isLunch) {
                        dayPlanning.lunch.meal
                    } else {
                        dayPlanning.dinner.meal
                    }
                }
            )
        }
    }
}