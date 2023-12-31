package com.gabr.gabc.qook.presentation.shared.components

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ClearAll
import androidx.compose.material.icons.outlined.KeyboardOptionKey
import androidx.compose.material3.HorizontalDivider
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
import com.gabr.gabc.qook.domain.user.User
import com.gabr.gabc.qook.presentation.shared.QDateUtils

@Composable
fun QPlanning(
    planning: List<DayPlanning>,
    modifier: Modifier = Modifier,
    users: List<User> = listOf(),
    onClearFullDayPlanning: (DayPlanning) -> Unit,
    onAddRecipeToDayPlanning: (DayPlanning, Boolean) -> Unit,
    onRecipeTapped: (Recipe, String) -> Unit,
    onClearDayPlanning: (DayPlanning, Boolean) -> Unit,
) {
    val today by remember { mutableStateOf(QDateUtils.getDayOfWeekIndex()) }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        planning.forEachIndexed { i, dp ->
            if (i == today) {
                QContentCard(
                    arrangement = Arrangement.Center,
                    alignment = Alignment.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                ) {
                    DayPlanningMetadata(
                        dp,
                        users,
                        true,
                        onAddRecipeToDayPlanning,
                        onRecipeTapped,
                        onClearDayPlanning,
                        onClearFullDayPlanning,
                    )
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                ) {
                    DayPlanningMetadata(
                        dp,
                        users,
                        false,
                        onAddRecipeToDayPlanning,
                        onRecipeTapped,
                        onClearDayPlanning,
                        onClearFullDayPlanning,
                    )
                }
            }
        }
    }
}

@Composable
fun DayPlanningMetadata(
    dp: DayPlanning,
    users: List<User> = listOf(),
    isToday: Boolean,
    onAddRecipeToDayPlanning: (DayPlanning, Boolean) -> Unit,
    onRecipeTapped: (Recipe, String) -> Unit,
    onClearDayPlanning: (DayPlanning, Boolean) -> Unit,
    onClearFullDayPlanning: (DayPlanning) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier.padding(horizontal = 12.dp),
    ) {
        Text(
            stringResource(QDateUtils.getWeekDayStringRes(dp.dayIndex)),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = {
            onClearFullDayPlanning(dp)
        }) {
            Icon(Icons.Outlined.ClearAll, "")
        }
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(end = 12.dp)
    ) {
        PlanningDayRecipe(
            dp,
            dp.lunch.meal,
            users,
            true,
            onAddRecipeToDayPlanning,
            onRecipeTapped,
            onClearDayPlanning,
        )
        PlanningDayRecipe(
            dp,
            dp.dinner.meal,
            users,
            false,
            onAddRecipeToDayPlanning,
            onRecipeTapped,
            onClearDayPlanning,
        )
    }
    if (!isToday) HorizontalDivider(
        color = MaterialTheme.colorScheme.outlineVariant,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
    )
}

@Composable
fun PlanningDayRecipe(
    dayPlanning: DayPlanning,
    recipe: Recipe,
    users: List<User> = listOf(),
    isLunch: Boolean,
    onAddRecipeToDayPlanning: (DayPlanning, Boolean) -> Unit,
    onRecipeTapped: (Recipe, String) -> Unit,
    onClearDayPlanning: (DayPlanning, Boolean) -> Unit,
) {
    val op = if (isLunch) {
        dayPlanning.lunch.op
    } else {
        dayPlanning.dinner.op
    }
    var recipeForOptions by remember { mutableStateOf(Recipe.EMPTY) }

    if (recipeForOptions != Recipe.EMPTY) {
        QDialog(
            onDismissRequest = { recipeForOptions = Recipe.EMPTY },
            leadingIcon = Icons.Outlined.KeyboardOptionKey,
            title = R.string.plannings_options_title,
            content = {
                Text(stringResource(R.string.plannings_options_description, recipeForOptions.name))
            },
            buttonTitle = R.string.plannings_replace,
            onSubmit = {
                onAddRecipeToDayPlanning(dayPlanning, isLunch)
                recipeForOptions = Recipe.EMPTY
            },
            buttonSecondaryTitle = R.string.plannings_remove,
            onSubmitSecondary = {
                onClearDayPlanning(dayPlanning, isLunch)
                recipeForOptions = Recipe.EMPTY
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
        if (recipe == Recipe.EMPTY) QAutoSizeText(
            stringResource(R.string.planning_no_recipe_added),
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.outline,
                fontWeight = FontWeight.Normal,
            ),
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp)
        )
        if (recipe == Recipe.EMPTY) {
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
                op = users.find { it.id == op },
                simplified = true,
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                onClick = {
                    onRecipeTapped(recipe, op)
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