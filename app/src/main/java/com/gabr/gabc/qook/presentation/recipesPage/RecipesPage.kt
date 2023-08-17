package com.gabr.gabc.qook.presentation.recipesPage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.presentation.recipesPage.viewModel.RecipesState
import com.gabr.gabc.qook.presentation.recipesPage.viewModel.RecipesViewModel
import com.gabr.gabc.qook.presentation.shared.components.QActionBar
import com.gabr.gabc.qook.presentation.shared.components.QEmptyBox
import com.gabr.gabc.qook.presentation.shared.components.QLoadingScreen
import com.gabr.gabc.qook.presentation.shared.components.QRecipeItem
import com.gabr.gabc.qook.presentation.shared.components.QTag
import com.gabr.gabc.qook.presentation.shared.components.QTextForm
import com.gabr.gabc.qook.presentation.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipesPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                RecipesView()
            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun RecipesView() {
        val viewModel: RecipesViewModel by viewModels()
        val state = viewModel.recipesState.collectAsState().value

        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(key1 = Unit) {
            viewModel.getRecipes { errorMessage ->
                scope.launch {
                    snackbarHostState.showSnackbar(errorMessage)
                }
            }
            viewModel.getTags { errorMessage ->
                scope.launch {
                    snackbarHostState.showSnackbar(errorMessage)
                }
            }
        }

        Box {
            Scaffold(
                topBar = {
                    QActionBar(title = R.string.recipes_title, onBack = { finish() })
                },
                snackbarHost = {
                    SnackbarHost(snackbarHostState)
                }
            ) {
                Box(
                    modifier = Modifier
                        .padding(it)
                        .consumeWindowInsets(it)
                ) {
                    RecipesContent(viewModel, state)
                }
            }
            if (viewModel.isLoading.value) QLoadingScreen()
        }
    }

    @Composable
    fun RecipesContent(viewModel: RecipesViewModel, state: RecipesState) {
        val focusManager = LocalFocusManager.current
        var searchField by remember { mutableStateOf("") }

        fun clearSearch() {
            searchField = ""
            viewModel.onSearchUpdate("")
            focusManager.clearFocus()
        }

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
        ) {
            QTextForm(
                labelId = R.string.recipes_search_recipes,
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
            Spacer(modifier = Modifier.size(8.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                content = {
                    items(state.tags) { tag ->
                        QTag(
                            tag = tag,
                            modifier = Modifier.padding(4.dp),
                            onClick = {
                                // TODO: Filter the search by tag
                            }
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.size(8.dp))
            if (state.recipes.isEmpty() || state.searchedRecipes.isEmpty()) {
                QEmptyBox(
                    message = R.string.recipes_empty,
                    icon = Icons.Outlined.MenuBook,
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(state.searchedRecipes) { recipe ->
                        QRecipeItem(recipe = recipe, modifier = Modifier.padding(8.dp))
                    }
                }
            }
        }
    }
}