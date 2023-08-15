package com.gabr.gabc.qook.presentation.addRecipePage.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.domain.tag.Tag
import com.gabr.gabc.qook.presentation.addRecipePage.viewModel.AddRecipeViewModel
import com.gabr.gabc.qook.presentation.shared.components.QTag

@Composable
fun RecipeTags(
    onNavigate: () -> Unit,
    onTagTap: (Tag) -> Unit,
    viewModel: AddRecipeViewModel
) {
    val state = viewModel.recipeState.collectAsState().value

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            stringResource(R.string.add_recipe_tags_description),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(12.dp)
        )
        if (state.createdTags.isEmpty()) {
            Text(
                stringResource(R.string.tags_empty),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(12.dp)
                    .weight(1f)
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(state.createdTags.size) { x ->
                    val tag = state.createdTags[x]
                    val selected = state.recipe.tags.contains(tag)

                    Surface(
                        onClick = {
                            if (!state.recipe.tags.contains(tag)) {
                                viewModel.addTag(tag)
                            } else {
                                viewModel.deleteTag(tag)
                            }
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(4.dp)
                        ) {
                            QTag(tag, enabled = true, onClick = {
                                onTagTap(tag)
                            }, icon = Icons.Outlined.Create)
                            Spacer(modifier = Modifier.weight(1f))
                            Surface(
                                modifier = Modifier
                                    .width(36.dp)
                                    .height(24.dp)
                                    .padding(end = 12.dp),
                                shape = CircleShape,
                                border = if (selected) {
                                    BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                                } else {
                                    BorderStroke(2.dp, MaterialTheme.colorScheme.outline)
                                }
                            ) {
                                if (selected) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = "",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        Button(
            onClick = {
                onNavigate()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 32.dp, end = 32.dp)
        ) {
            Text(stringResource(R.string.add_recipe_next))
        }
    }
}