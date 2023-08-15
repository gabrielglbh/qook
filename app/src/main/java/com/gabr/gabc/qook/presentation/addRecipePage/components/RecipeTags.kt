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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
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
import com.gabr.gabc.qook.presentation.theme.seed

@Composable
fun RecipeTags(
    onNavigate: () -> Unit,
    viewModel: AddRecipeViewModel
) {
    val state = viewModel.recipeState.collectAsState()

    val selectedTags = remember {
        mutableStateListOf<Tag>().apply {
            addAll(state.value.recipe.tags)
        }
    }
    val createdTags = remember {
        mutableStateListOf<Tag>().apply {
            addAll(state.value.createdTags)
        }
    }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            stringResource(R.string.add_recipe_tags_description),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(12.dp)
        )
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(createdTags.size) { x ->
                val tag = createdTags[x]
                val selected = selectedTags.contains(tag)
                TagElementInList(tag, selected) {
                    if (!selectedTags.contains(tag)) {
                        selectedTags.add(tag)
                    } else {
                        selectedTags.remove(tag)
                    }
                }
            }
        }
        Button(
            onClick = {
                viewModel.updateTags(selectedTags)
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

@Composable
fun TagElementInList(tag: Tag, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.height(48.dp),
        onClick = { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            QTag(tag)
            Spacer(modifier = Modifier.weight(1f))
            Surface(
                modifier = Modifier.size(24.dp),
                shape = CircleShape,
                border = if (selected) {
                    BorderStroke(2.dp, seed)
                } else {
                    BorderStroke(2.dp, MaterialTheme.colorScheme.outline)
                }
            ) {
                if (selected) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "",
                        tint = seed,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}