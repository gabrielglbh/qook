package com.gabr.gabc.qook.presentation.addRecipePage

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import arrow.core.Either
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.domain.recipe.Recipe
import com.gabr.gabc.qook.domain.tag.Tag
import com.gabr.gabc.qook.presentation.addRecipePage.components.RecipeDescription
import com.gabr.gabc.qook.presentation.addRecipePage.components.RecipeIngredients
import com.gabr.gabc.qook.presentation.addRecipePage.components.RecipeMetadataForm
import com.gabr.gabc.qook.presentation.addRecipePage.components.RecipePreview
import com.gabr.gabc.qook.presentation.addRecipePage.components.RecipeTags
import com.gabr.gabc.qook.presentation.addRecipePage.viewModel.AddRecipeViewModel
import com.gabr.gabc.qook.presentation.addTagPage.AddTagPage
import com.gabr.gabc.qook.presentation.addTagPage.AlteredMode
import com.gabr.gabc.qook.presentation.recipeDetailsPage.RecipeDetailsPage
import com.gabr.gabc.qook.presentation.shared.components.QActionBar
import com.gabr.gabc.qook.presentation.shared.components.QLoadingScreen
import com.gabr.gabc.qook.presentation.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddRecipePage : ComponentActivity() {
    companion object {
        const val UPDATE_TAG = "UPDATE_TAG"
        const val RECIPE_UPDATED = "RECIPE_UPDATED"
    }

    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.forEach { actionMap ->
                when (actionMap.key) {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE -> {
                        if (!actionMap.value) {
                            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        }
                    }

                    Manifest.permission.READ_EXTERNAL_STORAGE -> {
                        if (actionMap.value) {
                            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        } else {
                            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                    }

                    Manifest.permission.READ_MEDIA_IMAGES -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            if (actionMap.value) {
                                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            } else {
                                shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES)
                            }
                        }
                    }
                }
            }
        }
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val extras = result.data
                val viewModel: AddRecipeViewModel by viewModels()

                val updatedTag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    extras?.getParcelableExtra(AddTagPage.HAS_ALTERED_TAG, Tag::class.java)
                } else {
                    extras?.getParcelableExtra(AddTagPage.HAS_ALTERED_TAG)
                }
                val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    extras?.getSerializableExtra(
                        AddTagPage.HAS_ALTERED_MODE,
                        AlteredMode::class.java
                    )
                } else {
                    extras?.getSerializableExtra(AddTagPage.HAS_ALTERED_MODE)
                }

                updatedTag?.let {
                    when (mode) {
                        AlteredMode.DELETE -> {
                            viewModel.deleteTagForLocalLoading(it.id)
                        }

                        AlteredMode.UPDATE -> {
                            viewModel.updateTagForLocalLoading(it)
                        }

                        AlteredMode.CREATE -> {
                            viewModel.createTagForLocalLoading(it)
                        }
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: AddRecipeViewModel by viewModels()

        val recipeFromDetails = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(RecipeDetailsPage.RECIPE_FROM_DETAILS, Recipe::class.java)
        } else {
            intent.getParcelableExtra(RecipeDetailsPage.RECIPE_FROM_DETAILS)
        }

        recipeFromDetails?.let {
            viewModel.loadLocalRecipe(it)
        }

        pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    viewModel.updateMetadata(photo = uri)
                }
            }

        setContent {
            AppTheme {
                AddRecipeView()
            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun AddRecipeView() {
        val viewModel: AddRecipeViewModel by viewModels()
        val state = viewModel.recipeState.collectAsState().value
        val colorScheme = MaterialTheme.colorScheme

        var currentPage by remember { mutableStateOf(RecipeStep.DATA) }
        var bar2Color by remember { mutableStateOf(colorScheme.outlineVariant) }
        var bar3Color by remember { mutableStateOf(colorScheme.outlineVariant) }
        var bar4Color by remember { mutableStateOf(colorScheme.outlineVariant) }
        var bar5Color by remember { mutableStateOf(colorScheme.outlineVariant) }
        val navController = rememberNavController()

        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }

        val errorOnUpload = stringResource(R.string.add_recipe_error_validation)
        val errorOnIngredients = stringResource(R.string.err_add_recipe_ingredients_empty)
        val contentPadding = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)

        navController.addOnDestinationChangedListener { _, dest, _ ->
            val steps = RecipeStep.values()
            currentPage = RecipeStep.valueOf(dest.route!!)
            bar2Color =
                if (currentPage in steps.slice(RecipeStep.INGREDIENTS.ordinal until steps.size)) {
                    colorScheme.primaryContainer
                } else {
                    colorScheme.outlineVariant
                }
            bar3Color =
                if (currentPage in steps.slice(RecipeStep.DESCRIPTION.ordinal until steps.size)) {
                    colorScheme.primaryContainer
                } else {
                    colorScheme.outlineVariant
                }
            bar4Color = if (currentPage in steps.slice(RecipeStep.TAGS.ordinal until steps.size)) {
                colorScheme.primaryContainer
            } else {
                colorScheme.outlineVariant
            }
            bar5Color = if (currentPage == RecipeStep.PREVIEW) {
                colorScheme.primaryContainer
            } else {
                colorScheme.outlineVariant
            }
        }

        Box {
            Scaffold(
                topBar = {
                    QActionBar(
                        title = R.string.add_recipe_title,
                        onBack = {
                            if (currentPage != RecipeStep.DATA) {
                                navController.popBackStack()
                            } else {
                                finish()
                            }
                        },
                        actionBehaviour = if (currentPage == RecipeStep.TAGS) {
                            {
                                val intent = Intent(this@AddRecipePage, AddTagPage::class.java)
                                resultLauncher.launch(intent)
                            }
                        } else {
                            null
                        },
                        action = if (currentPage == RecipeStep.TAGS) {
                            Either.Right(Icons.Outlined.BookmarkAdd)
                        } else {
                            null
                        }
                    )
                },
                snackbarHost = {
                    SnackbarHost(snackbarHostState)
                }
            ) {
                Box(
                    contentAlignment = Alignment.TopCenter,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                        .consumeWindowInsets(it)
                ) {
                    Column(
                        verticalArrangement = Arrangement.SpaceAround,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        RecipeProgressBar(
                            navController = navController,
                            colorBar2 = bar2Color,
                            colorBar3 = bar3Color,
                            colorBar4 = bar4Color,
                            colorBar5 = bar5Color
                        )
                        Spacer(modifier = Modifier.size(12.dp))
                        NavHost(
                            navController = navController,
                            startDestination = RecipeStep.DATA.name,
                            modifier = Modifier.weight(1f)
                        ) {
                            composable(RecipeStep.DATA.name) {
                                RecipeMetadataForm(
                                    modifier = contentPadding,
                                    onNavigate = {
                                        navController.navigate(RecipeStep.INGREDIENTS.name)
                                    },
                                    requestMultiplePermissions = requestMultiplePermissions,
                                    viewModel = viewModel
                                )
                            }
                            composable(RecipeStep.INGREDIENTS.name) {
                                RecipeIngredients(
                                    modifier = contentPadding,
                                    onNavigate = { navController.navigate(RecipeStep.DESCRIPTION.name) },
                                    viewModel = viewModel,
                                    onIngredientsEmpty = {
                                        scope.launch {
                                            snackbarHostState.showSnackbar(errorOnIngredients)
                                        }
                                    }
                                )
                            }
                            composable(RecipeStep.DESCRIPTION.name) {
                                RecipeDescription(
                                    modifier = contentPadding,
                                    onNavigate = {
                                        navController.navigate(RecipeStep.TAGS.name)
                                    },
                                    viewModel = viewModel
                                )
                            }
                            composable(RecipeStep.TAGS.name) {
                                RecipeTags(
                                    modifier = contentPadding,
                                    onNavigate = {
                                        navController.navigate(RecipeStep.PREVIEW.name)
                                    },
                                    onTagTap = { tag ->
                                        val intent =
                                            Intent(this@AddRecipePage, AddTagPage::class.java)
                                        intent.putExtra(UPDATE_TAG, tag)
                                        resultLauncher.launch(intent)
                                    },
                                    viewModel = viewModel
                                )
                            }
                            composable(RecipeStep.PREVIEW.name) {
                                RecipePreview(
                                    modifier = contentPadding,
                                    viewModel = viewModel,
                                    onError = { error ->
                                        scope.launch {
                                            snackbarHostState.showSnackbar(error ?: errorOnUpload)
                                        }
                                    },
                                    onSuccess = {
                                        viewModel.uploadRecipe(
                                            ifError = { error ->
                                                scope.launch {
                                                    snackbarHostState.showSnackbar(error)
                                                }
                                            },
                                            ifSuccess = {
                                                if (state.originalRecipe != Recipe.EMPTY_RECIPE || state.recipe != Recipe.EMPTY_RECIPE) {
                                                    val resultIntent = Intent()
                                                    resultIntent.putExtra(
                                                        RECIPE_UPDATED,
                                                        state.recipe
                                                    )
                                                    setResult(RESULT_OK, resultIntent)
                                                }
                                                finish()
                                            }
                                        )
                                    }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.size(8.dp))
                    }
                }
            }
            if (viewModel.isLoading.value) QLoadingScreen()
        }
    }

    @Composable
    fun RecipeProgressBar(
        navController: NavController,
        colorBar2: Color,
        colorBar3: Color,
        colorBar4: Color,
        colorBar5: Color
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp)
        ) {
            Bar(
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                navController.navigate(RecipeStep.DATA.name)
            }
            Spacer(modifier = Modifier.size(8.dp))
            Bar(modifier = Modifier.weight(1f), color = colorBar2) {
                navController.navigate(RecipeStep.INGREDIENTS.name)
            }
            Spacer(modifier = Modifier.size(8.dp))
            Bar(modifier = Modifier.weight(1f), color = colorBar3) {
                navController.navigate(RecipeStep.DESCRIPTION.name)
            }
            Spacer(modifier = Modifier.size(8.dp))
            Bar(modifier = Modifier.weight(1f), color = colorBar4) {
                navController.navigate(RecipeStep.TAGS.name)
            }
            Spacer(modifier = Modifier.size(8.dp))
            Bar(modifier = Modifier.weight(1f), color = colorBar5) {
                navController.navigate(RecipeStep.PREVIEW.name)
            }
        }
    }

    @Composable
    fun Bar(modifier: Modifier, color: Color, onClick: () -> Unit) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = color,
            modifier = modifier
                .height(8.dp),
            onClick = {
                onClick()
            }
        ) {}
    }
}