package com.gabr.gabc.qook.presentation.shared.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.Stairs
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.domain.recipe.Easiness
import com.gabr.gabc.qook.domain.recipe.Recipe

@Composable
fun QRecipeDetail(recipe: Recipe, modifier: Modifier) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
    ) {
        QImageCircle(
            uri = recipe.photo,
            placeholder = Icons.Outlined.Photo,
        )
        Spacer(modifier = Modifier.size(12.dp))
        Text(
            recipe.name,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.size(12.dp))
        QContentCard {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.height(36.dp)
            ) {
                Icon(
                    Icons.Outlined.Stairs,
                    "",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    when (recipe.easiness) {
                        Easiness.EASY -> stringResource(R.string.add_recipe_easiness_EASY)
                        Easiness.MEDIUM -> stringResource(R.string.add_recipe_easiness_MEDIUM)
                        Easiness.HARD -> stringResource(R.string.add_recipe_easiness_HARD)
                    },
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier.weight(1f)
                )
                Divider(
                    modifier = Modifier
                        .height(16.dp)
                        .width(1.dp)
                        .padding(horizontal = 4.dp)
                )
                Icon(
                    Icons.Outlined.AccessTime,
                    "",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    recipe.time,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.size(8.dp))
            LazyRow(
                content = {
                    items(recipe.tags) { tag ->
                        Box(
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            QTag(tag = tag)
                        }
                    }
                }
            )
        }
        Spacer(modifier = Modifier.size(12.dp))
        QContentCard {
            Text(
                stringResource(R.string.recipe_details_ingredients),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                ),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.size(12.dp))
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                recipe.ingredients.forEach {
                    Text(
                        it,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
        Spacer(modifier = Modifier.size(12.dp))
        QContentCard {
            Text(
                stringResource(R.string.recipe_details_steps),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                ),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                recipe.description, textAlign = TextAlign.Justify,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}