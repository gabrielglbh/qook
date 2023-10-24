package com.gabr.gabc.qook.presentation.recipeDetailsPage

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ModeEdit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.domain.recipe.Recipe
import com.gabr.gabc.qook.domain.user.User
import com.gabr.gabc.qook.presentation.addRecipePage.AddRecipePage
import com.gabr.gabc.qook.presentation.recipeDetailsPage.viewModel.RecipeDetailsViewModel
import com.gabr.gabc.qook.presentation.shared.IntentVars.Companion.ALLOW_TO_UPDATE
import com.gabr.gabc.qook.presentation.shared.IntentVars.Companion.HAS_DELETED_RECIPE
import com.gabr.gabc.qook.presentation.shared.IntentVars.Companion.HAS_UPDATED_RECIPE
import com.gabr.gabc.qook.presentation.shared.IntentVars.Companion.RECIPE
import com.gabr.gabc.qook.presentation.shared.IntentVars.Companion.RECIPE_FROM_DETAILS
import com.gabr.gabc.qook.presentation.shared.IntentVars.Companion.RECIPE_OP
import com.gabr.gabc.qook.presentation.shared.IntentVars.Companion.RECIPE_UPDATED
import com.gabr.gabc.qook.presentation.shared.components.QActionBar
import com.gabr.gabc.qook.presentation.shared.components.QDialog
import com.gabr.gabc.qook.presentation.shared.components.QLoadingScreen
import com.gabr.gabc.qook.presentation.shared.components.QRecipeDetail
import com.gabr.gabc.qook.presentation.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipeDetailsPage : ComponentActivity() {
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val extras = result.data?.extras

                val viewModel: RecipeDetailsViewModel by viewModels()
                val updatedRecipe = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    extras?.getParcelable(RECIPE_UPDATED, Recipe::class.java)
                } else {
                    extras?.getParcelable(RECIPE_UPDATED)
                }

                updatedRecipe?.let {
                    viewModel.updateRecipe(it)
                    viewModel.isUpdating(true)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: RecipeDetailsViewModel by viewModels()

        val recipeFromList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(RECIPE, Recipe::class.java)
        } else {
            intent.getParcelableExtra(RECIPE)
        }

        viewModel.canUpdate(intent.getBooleanExtra(ALLOW_TO_UPDATE, true))
        recipeFromList?.let { viewModel.updateRecipe(it) }

        val op = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(RECIPE_OP, User::class.java)
        } else {
            intent.getParcelableExtra(RECIPE_OP)
        }

        viewModel.updateOp(op)

        // TODO: .well-known/assetlinks.json must be updated with keystore SHA-256
        handleAppLinkIntent(intent)

        setContent {
            AppTheme {
                RecipeDetailsView()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleAppLinkIntent(intent)
    }

    private fun handleAppLinkIntent(intent: Intent) {
        val viewModel: RecipeDetailsViewModel by viewModels()
        val appLinkAction = intent.action
        val appLinkData = intent.data
        if (Intent.ACTION_VIEW == appLinkAction) {
            appLinkData?.let { uri ->
                uri.lastPathSegment?.let { segment ->
                    val ids = segment.split('-')
                    viewModel.loadRecipe(ids[0], ids[1]) {
                        finish()
                    }
                }
            }
        }
    }

    @Composable
    fun RecipeDetailsView() {
        val viewModel: RecipeDetailsViewModel by viewModels()
        val op = viewModel.op.value
        val currentUid = viewModel.currentUserUid.value
        val recipe = viewModel.recipe.value

        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }
        val showConfirmationDialog = remember { mutableStateOf(false) }

        val errOpenLink = stringResource(R.string.err_recipe_details_open_link)
        val onSaveRecipe = stringResource(R.string.recipe_details_add_recipe_success)

        val link =
            currentUid?.let {
                stringResource(
                    R.string.deepLinkViewRecipe,
                    "${recipe.id}-${op ?: it}"
                )
            }

        if (showConfirmationDialog.value)
            QDialog(
                onDismissRequest = { showConfirmationDialog.value = false },
                leadingIcon = Icons.Outlined.Delete,
                buttonTitle = R.string.recipe_details_remove_recipe_button,
                title = R.string.recipe_details_remove_recipe,
                content = {
                    Text(
                        stringResource(
                            R.string.recipe_details_delete_warning,
                            viewModel.recipe.value.name
                        )
                    )
                },
                onSubmit = {
                    showConfirmationDialog.value = false
                    viewModel.removeRecipe(
                        onError = {
                            scope.launch {
                                snackbarHostState.showSnackbar(it)
                            }
                        },
                        onSuccess = {
                            val intent = Intent()
                            intent.putExtra(
                                HAS_DELETED_RECIPE,
                                viewModel.recipe.value
                            )
                            setResult(RESULT_OK, intent)
                            finish()
                        }
                    )
                },
            )

        Box(
            contentAlignment = Alignment.BottomCenter
        ) {
            Scaffold(
                topBar = {
                    QActionBar(
                        title = R.string.recipe_details,
                        onBack =
                        {
                            if (viewModel.isUpdate.value) {
                                val resultIntent = Intent()
                                resultIntent.putExtra(
                                    HAS_UPDATED_RECIPE,
                                    viewModel.recipe.value
                                )
                                setResult(RESULT_OK, resultIntent)
                            }
                            finish()
                        },
                        actions = if (viewModel.canUpdate.value) {
                            listOf(
                                {
                                    IconButton(
                                        modifier = Modifier.padding(end = 8.dp),
                                        onClick = {
                                            showConfirmationDialog.value = true
                                        }
                                    ) {
                                        Icon(
                                            Icons.Outlined.Delete,
                                            "",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                },
                                {
                                    IconButton(
                                        onClick = {
                                            val intent = Intent(
                                                this@RecipeDetailsPage,
                                                AddRecipePage::class.java
                                            )
                                            intent.putExtra(
                                                RECIPE_FROM_DETAILS,
                                                viewModel.recipe.value
                                            )
                                            resultLauncher.launch(intent)
                                        }
                                    ) {
                                        Icon(Icons.Outlined.ModeEdit, "")
                                    }
                                })
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
                    modifier = Modifier
                        .padding(it)
                        .consumeWindowInsets(it)
                ) {
                    Column {
                        QRecipeDetail(
                            recipe = viewModel.recipe.value,
                            modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
                            op = op,
                            shareRecipe = {
                                val intent = Intent().setAction(Intent.ACTION_SEND)
                                intent.type = "text/plain"
                                intent.putExtra(
                                    Intent.EXTRA_TEXT,
                                    "${getString(R.string.share_recipe_description)}\n" +
                                            link
                                )
                                startActivity(
                                    Intent.createChooser(
                                        intent,
                                        getString(R.string.share_recipe)
                                    )
                                )
                            },
                            addToOwnRecipesButton = if (op != null && op.id != viewModel.currentUserUid.value) {
                                {
                                    Button(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 12.dp, end = 12.dp, bottom = 16.dp),
                                        onClick = {
                                            viewModel.addToMyOwnRecipes(
                                                onError = { error ->
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar(error)
                                                    }
                                                },
                                                onSave = {
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar(onSaveRecipe)
                                                    }
                                                }
                                            )
                                        }
                                    ) {
                                        Text(stringResource(R.string.plannings_add_to_my_recipes))
                                    }
                                }
                            } else {
                                null
                            },
                            onRecipeUrlClick = {
                                try {
                                    startActivity(
                                        Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse(viewModel.recipe.value.recipeUrl)
                                        )
                                    )
                                } catch (_: ActivityNotFoundException) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(errOpenLink)
                                    }
                                }
                            }
                        )
                    }
                }
            }
            if (viewModel.isLoading.value) QLoadingScreen()
        }
    }
}