package com.gabr.gabc.qook.presentation.addRecipePage.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.domain.tag.Tag
import com.gabr.gabc.qook.presentation.addRecipePage.viewModel.AddRecipeViewModel
import com.gabr.gabc.qook.presentation.shared.components.QEmptyBox
import com.gabr.gabc.qook.presentation.shared.components.QTag
import com.gabr.gabc.qook.presentation.shared.components.QTextForm
import com.gabr.gabc.qook.presentation.shared.components.QTextTitle

@Composable
fun RecipeTags(
    modifier: Modifier,
    onNavigate: () -> Unit,
    onTagTap: (Tag) -> Unit,
    viewModel: AddRecipeViewModel
) {
    val focusManager = LocalFocusManager.current
    val state = viewModel.recipeState.collectAsState().value

    var searchField by remember { mutableStateOf("") }

    fun clearSearch() {
        searchField = ""
        viewModel.onSearchUpdate("")
        focusManager.clearFocus()
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        QTextTitle(
            title = R.string.add_recipe_tags_title,
            subtitle = R.string.add_recipe_tags_description
        )
        Spacer(modifier = Modifier.size(8.dp))
        QTextForm(
            labelId = R.string.add_recipe_search_tags,
            value = searchField,
            onValueChange = {
                searchField = it
                viewModel.onSearchUpdate(it)
            },
            leadingIcon = Icons.Outlined.Search,
            trailingIcon = if (searchField.isEmpty()) {
                null
            } else {
                {
                    IconButton(onClick = { clearSearch() }) {
                        Icon(Icons.Outlined.Clear, contentDescription = null)
                    }
                }
            },
            imeAction = ImeAction.Search,
            onSubmitWithImeAction = {
                viewModel.onSearchUpdate(searchField)
                focusManager.clearFocus()
            }
        )
        if (state.createdTags.isEmpty() || state.searchedTags.isEmpty()) {
            QEmptyBox(
                message = R.string.tags_empty,
                icon = Icons.Outlined.Bookmarks,
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 8.dp)
            ) {
                items(state.searchedTags.size) { x ->
                    val tag = state.searchedTags[x]
                    val selected = state.recipe.tags.contains(tag)

                    Surface(
                        onClick = {
                            clearSearch()
                            if (!state.recipe.tags.contains(tag)) {
                                viewModel.addTagToRecipe(tag)
                            } else {
                                viewModel.deleteTagFromRecipe(tag)
                            }
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(4.dp)
                        ) {
                            QTag(
                                tag,
                                enabled = true,
                                icon = Icons.Outlined.Create,
                                onClick = {
                                    clearSearch()
                                    onTagTap(tag)
                                }
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Surface(
                                modifier = Modifier.size(24.dp),
                                shape = CircleShape,
                                border = if (selected) {
                                    BorderStroke(2.dp, MaterialTheme.colorScheme.primaryContainer)
                                } else {
                                    BorderStroke(2.dp, MaterialTheme.colorScheme.outline)
                                }
                            ) {
                                if (selected) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = "",
                                        tint = MaterialTheme.colorScheme.primaryContainer,
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
                if (state.recipe.tags.isEmpty()) return@Button
                onNavigate()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text(stringResource(R.string.add_recipe_ready))
        }
    }
}