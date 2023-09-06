package com.gabr.gabc.qook.presentation.sharedPlanningPage

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.domain.planning.DayPlanning
import com.gabr.gabc.qook.domain.recipe.Recipe
import com.gabr.gabc.qook.presentation.homePage.HomePage
import com.gabr.gabc.qook.presentation.ownPlanningPage.OwnPlanningPage
import com.gabr.gabc.qook.presentation.recipeDetailsPage.RecipeDetailsPage
import com.gabr.gabc.qook.presentation.recipesPage.RecipesPage
import com.gabr.gabc.qook.presentation.shared.components.QActionBar
import com.gabr.gabc.qook.presentation.shared.components.QContentCard
import com.gabr.gabc.qook.presentation.shared.components.QImageContainer
import com.gabr.gabc.qook.presentation.shared.components.QLoadingScreen
import com.gabr.gabc.qook.presentation.shared.components.QPlanning
import com.gabr.gabc.qook.presentation.shared.components.QTextTitle
import com.gabr.gabc.qook.presentation.sharedPlanningPage.viewModel.SharedPlanningViewModel
import com.gabr.gabc.qook.presentation.shoppingListPage.ShoppingListPage
import com.gabr.gabc.qook.presentation.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SharedPlanningPage : ComponentActivity() {
    companion object {
        const val GROUP_ID = "GROUP_ID"
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val extras = result.data?.extras

                val viewModel: SharedPlanningViewModel by viewModels()
                val updatedPlanning = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    extras?.getParcelable(RecipesPage.HAS_UPDATED_PLANNING, DayPlanning::class.java)
                } else {
                    extras?.getParcelable(RecipesPage.HAS_UPDATED_PLANNING)
                }
                val updatedRecipeForPlanning =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        extras?.getParcelable(
                            RecipesPage.HAS_UPDATED_PLANNING_RECIPE,
                            Recipe::class.java
                        )
                    } else {
                        extras?.getParcelable(RecipesPage.HAS_UPDATED_PLANNING_RECIPE)
                    }
                val isLunch = extras?.getBoolean(OwnPlanningPage.IS_LUNCH) ?: false
                updatedPlanning?.let { dayPlanning ->
                    updatedRecipeForPlanning?.let { recipe ->
                        viewModel.updatePlanningLocally(
                            if (isLunch) {
                                dayPlanning.copy(lunch = recipe)
                            } else {
                                dayPlanning.copy(dinner = recipe)
                            }
                        )
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: .well-known/assetlinks.json must be updated with keystore SHA-256
        handleAppLinkIntent(intent)

        setContent {
            AppTheme {
                SharedPlanningView()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleAppLinkIntent(intent)
    }

    private fun handleAppLinkIntent(intent: Intent) {
        val viewModel: SharedPlanningViewModel by viewModels()
        val appLinkAction = intent.action
        val appLinkData = intent.data
        if (Intent.ACTION_VIEW == appLinkAction) {
            appLinkData?.lastPathSegment?.also { groupId ->
                viewModel.addUserToGroup(groupId) {}
            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun SharedPlanningView() {
        val viewModel: SharedPlanningViewModel by viewModels()
        val group = viewModel.sharedPlanning.value

        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(key1 = Unit, block = {
            val groupId = intent.getStringExtra(GROUP_ID)
            groupId?.let {
                viewModel.getSharedPlanning(it) { err ->
                    scope.launch {
                        snackbarHostState.showSnackbar(err)
                    }
                }
            }
        })

        Box {
            Scaffold(
                topBar = {
                    QActionBar(
                        title = R.string.shared_planning_title,
                        onBack = {
                            finish()
                        },
                        actions = listOf {
                            IconButton(
                                onClick = {
                                    val intent = Intent().setAction(Intent.ACTION_SEND)
                                    intent.type = "text/plain"
                                    intent.putExtra(
                                        Intent.EXTRA_TEXT,
                                        "${getString(R.string.shared_planning_message_on_code_sharing)}\n" +
                                                "${getString(R.string.deepLinkJoinGroup)}${group.id}"
                                    )
                                    startActivity(
                                        Intent.createChooser(
                                            intent,
                                            getString(R.string.shared_planning_group_code)
                                        )
                                    )
                                }
                            ) {
                                Icon(Icons.Outlined.Share, "")
                            }
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
                    // TODO: Admin to only modify shared group
                    Column(
                        verticalArrangement = Arrangement.SpaceAround,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        QImageContainer(
                            uri = group.photo,
                            placeholder = Icons.Outlined.Group,
                            size = 100.dp
                        )
                        Spacer(modifier = Modifier.size(12.dp))
                        QTextTitle(
                            rawTitle = group.name,
                            subtitle = R.string.shared_planning_info_display
                        )
                        Spacer(modifier = Modifier.size(12.dp))
                        QContentCard(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .fillMaxWidth(),
                            arrangement = Arrangement.Top,
                            alignment = Alignment.CenterHorizontally,
                            onClick = {
                                val intent =
                                    Intent(this@SharedPlanningPage, ShoppingListPage::class.java)
                                intent.putExtra(
                                    HomePage.HOME_PLANNING,
                                    group.planning.toTypedArray()
                                )
                                intent.putExtra(GROUP_ID, group.id)
                                startActivity(intent)
                            }
                        ) {
                            Row {
                                Text(
                                    stringResource(R.string.home_shopping_bnb),
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(Icons.Outlined.KeyboardArrowRight, "")
                            }
                        }
                        Spacer(modifier = Modifier.size(12.dp))
                        QPlanning(
                            group.planning,
                            onClearDayPlanning = { dp ->
                                viewModel.updatePlanning(
                                    dp.copy(
                                        lunch = Recipe.EMPTY_RECIPE,
                                        dinner = Recipe.EMPTY_RECIPE
                                    )
                                ) { }
                            },
                            onAddRecipeToDayPlanning = { dp, isLunch ->
                                val intent =
                                    Intent(this@SharedPlanningPage, RecipesPage::class.java)
                                intent.putExtra(
                                    RecipesPage.RECIPES_LIST,
                                    viewModel.recipes.value?.toTypedArray()
                                )
                                intent.putExtra(OwnPlanningPage.FROM_PLANNING, dp)
                                intent.putExtra(OwnPlanningPage.IS_LUNCH, isLunch)
                                resultLauncher.launch(intent)
                            },
                            onRecipeTapped = { recipe ->
                                val intent =
                                    Intent(this@SharedPlanningPage, RecipeDetailsPage::class.java)
                                intent.putExtra(RecipeDetailsPage.RECIPE, recipe)
                                intent.putExtra(RecipeDetailsPage.ALLOW_TO_UPDATE, false)
                                startActivity(intent)
                            },
                        )
                    }
                }
            }
            if (viewModel.isLoading.value) QLoadingScreen()
        }
    }
}