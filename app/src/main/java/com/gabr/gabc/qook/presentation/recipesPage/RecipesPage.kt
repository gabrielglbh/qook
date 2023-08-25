package com.gabr.gabc.qook.presentation.recipesPage

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.outlined.PostAdd
import androidx.compose.material.icons.outlined.Receipt
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
import com.gabr.gabc.qook.domain.recipe.Recipe
import com.gabr.gabc.qook.domain.tag.Tag
import com.gabr.gabc.qook.presentation.addRecipePage.AddRecipePage
import com.gabr.gabc.qook.presentation.recipeDetailsPage.RecipeDetailsPage
import com.gabr.gabc.qook.presentation.recipesPage.viewModel.RecipesState
import com.gabr.gabc.qook.presentation.recipesPage.viewModel.RecipesViewModel
import com.gabr.gabc.qook.presentation.shared.components.QActionBar
import com.gabr.gabc.qook.presentation.shared.components.QEmptyBox
import com.gabr.gabc.qook.presentation.shared.components.QLoadingScreen
import com.gabr.gabc.qook.presentation.shared.components.QRecipeItem
import com.gabr.gabc.qook.presentation.shared.components.QShimmer
import com.gabr.gabc.qook.presentation.shared.components.QTag
import com.gabr.gabc.qook.presentation.shared.components.QTextForm
import com.gabr.gabc.qook.presentation.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipesPage : ComponentActivity() {
    companion object {
        const val RECIPE_FROM_LIST = "RECIPE_FROM_LIST"
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val extras = result.data?.extras

                val viewModel: RecipesViewModel by viewModels()
                val updatedRecipe = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    extras?.getParcelable(RecipeDetailsPage.HAS_UPDATED_RECIPE, Recipe::class.java)
                } else {
                    extras?.getParcelable(RecipeDetailsPage.HAS_UPDATED_RECIPE)
                }

                // TODO: Maintain the scroll? --- possible with lazy loading?
                if (updatedRecipe != null) {
                    updatedRecipe.let {
                        viewModel.updateRecipeLocally(it)
                    }
                } else {
                    val toBeAddedRecipe =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            extras?.getParcelable(AddRecipePage.RECIPE_UPDATED, Recipe::class.java)
                        } else {
                            extras?.getParcelable(AddRecipePage.RECIPE_UPDATED)
                        }

                    toBeAddedRecipe?.let { recipe ->
                        viewModel.addRecipeLocally(recipe)
                    }
                }

                val deletedRecipe = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    extras?.getParcelable(RecipeDetailsPage.HAS_DELETED_RECIPE, Recipe::class.java)
                } else {
                    extras?.getParcelable(RecipeDetailsPage.HAS_DELETED_RECIPE)
                }

                deletedRecipe?.let {
                    viewModel.deleteRecipeLocally(it)
                }
            }
        }

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
                    QActionBar(
                        title = R.string.recipes_title,
                        onBack = { finish() },
                        actions = listOf {
                            IconButton(
                                onClick = {
                                    val intent =
                                        Intent(this@RecipesPage, AddRecipePage::class.java)
                                    resultLauncher.launch(intent)
                                }
                            ) {
                                Icon(Icons.Outlined.PostAdd, "")
                            }
                        }
                    )
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
            if (viewModel.isLoadingRecipes.value || viewModel.isLoadingTags.value) QLoadingScreen()
        }
    }

    @Composable
    fun RecipesContent(viewModel: RecipesViewModel, state: RecipesState) {
        val searchState = viewModel.searchState.collectAsState().value

        val focusManager = LocalFocusManager.current
        var selectedFilterTag by remember { mutableStateOf<Tag?>(null) }

        fun clearSearch() {
            viewModel.updateSearchState(searchState.copy(query = ""))
            viewModel.clearSearch()
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
                value = searchState.query,
                onValueChange = {
                    viewModel.updateSearchState(searchState.copy(query = it))
                },
                leadingIcon = Icons.Outlined.Search,
                trailingIcon = if (searchState.query.isEmpty()) {
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
                    selectedFilterTag = null
                    viewModel.onSearch()
                    focusManager.clearFocus()
                }
            )
            Spacer(modifier = Modifier.size(8.dp))
            QShimmer(controller = state.tags.isNotEmpty()) { modifier ->
                LazyRow(
                    modifier = modifier.fillMaxWidth(),
                    content = {
                        items(state.tags) { tag ->
                            QTag(
                                tag = tag,
                                modifier = Modifier.padding(4.dp),
                                isActive = selectedFilterTag == tag,
                                onClick = {
                                    selectedFilterTag = if (selectedFilterTag == tag) {
                                        null
                                    } else {
                                        tag
                                    }
                                    viewModel.updateSearchState(
                                        searchState.copy(
                                            tag = selectedFilterTag,
                                            query = ""
                                        )
                                    )
                                    viewModel.onSearch()
                                }
                            )
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.size(8.dp))
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = if (state.recipes.isEmpty() || state.searchedRecipes.isEmpty()) {
                    Alignment.Center
                } else {
                    Alignment.TopCenter
                }
            ) {
                QShimmer(controller = state.recipes.isEmpty() || state.searchedRecipes.isEmpty()) { modifier ->
                    QEmptyBox(
                        message = R.string.recipes_empty,
                        icon = Icons.Outlined.Receipt,
                        modifier = modifier
                    )
                }
                QShimmer(controller = state.searchedRecipes.isNotEmpty()) { modifier ->
                    LazyColumn(
                        modifier = modifier
                    ) {
                        items(state.searchedRecipes) { recipe ->
                            QRecipeItem(recipe = recipe, modifier = Modifier.padding(8.dp)) {
                                val intent =
                                    Intent(this@RecipesPage, RecipeDetailsPage::class.java)
                                intent.putExtra(RECIPE_FROM_LIST, recipe)
                                resultLauncher.launch(intent)
                            }
                        }
                    }
                }
            }
        }
    }
}