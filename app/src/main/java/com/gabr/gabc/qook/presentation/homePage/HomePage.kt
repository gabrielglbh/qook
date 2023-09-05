package com.gabr.gabc.qook.presentation.homePage

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.DoubleArrow
import androidx.compose.material.icons.outlined.NightlightRound
import androidx.compose.material.icons.outlined.PostAdd
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.domain.planning.DayPlanning
import com.gabr.gabc.qook.domain.recipe.Recipe
import com.gabr.gabc.qook.domain.user.User
import com.gabr.gabc.qook.presentation.addRecipePage.AddRecipePage
import com.gabr.gabc.qook.presentation.homePage.viewModel.HomeViewModel
import com.gabr.gabc.qook.presentation.homePage.viewModel.UserState
import com.gabr.gabc.qook.presentation.planningPage.PlanningPage
import com.gabr.gabc.qook.presentation.profilePage.ProfilePage
import com.gabr.gabc.qook.presentation.recipeDetailsPage.RecipeDetailsPage
import com.gabr.gabc.qook.presentation.recipesPage.RecipesPage
import com.gabr.gabc.qook.presentation.shared.QDateUtils
import com.gabr.gabc.qook.presentation.shared.components.QActionBar
import com.gabr.gabc.qook.presentation.shared.components.QContentCard
import com.gabr.gabc.qook.presentation.shared.components.QImage
import com.gabr.gabc.qook.presentation.shared.components.QShimmer
import com.gabr.gabc.qook.presentation.shoppingListPage.ShoppingListPage
import com.gabr.gabc.qook.presentation.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomePage : ComponentActivity() {
    companion object {
        const val HOME_USER = "HOME_USER"
        const val HOME_PLANNING = "HOME_PLANNING"
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val extras = result.data?.extras
                val viewModel: HomeViewModel by viewModels()

                val scope = CoroutineScope(Dispatchers.Main)
                val snackbarHostState = SnackbarHostState()

                if (extras?.getBoolean(ProfilePage.HAS_UPDATED_PROFILE) == true) {
                    viewModel.getUser { errorMessage ->
                        scope.launch {
                            snackbarHostState.showSnackbar(errorMessage)
                        }
                    }
                }

                val updatedPlanning = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    extras?.getParcelableArray(
                        PlanningPage.HAS_UPDATED_PLANNING,
                        DayPlanning::class.java
                    )
                } else {
                    extras?.getParcelableArray(PlanningPage.HAS_UPDATED_PLANNING)
                }

                updatedPlanning?.let { p -> viewModel.updatePlanningLocally(p.map { it as DayPlanning }) }
            }
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { permission ->
            if (!permission) {
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            AppTheme {
                HomeView()
            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Preview
    @Composable
    fun HomeView() {
        val viewModel: HomeViewModel by viewModels()
        val state = viewModel.userState.collectAsState()

        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }
        val emptyRecipesMsg = stringResource(R.string.err_home_empty_recipes)

        LaunchedEffect(key1 = Unit) {
            viewModel.getUser { errorMessage ->
                scope.launch {
                    snackbarHostState.showSnackbar(errorMessage)
                }
            }
            viewModel.getPlanning()
        }

        Scaffold(
            topBar = {
                QActionBar(
                    actions = listOf {
                        UserPhotoIcon(state.value.user)
                    }
                )
            },
            snackbarHost = {
                SnackbarHost(snackbarHostState)
            },
            floatingActionButton = {
                FloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    onClick = {
                        startActivity(Intent(this@HomePage, AddRecipePage::class.java))
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Icon(Icons.Outlined.PostAdd, "")
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            stringResource(R.string.home_add_recipe_bnb),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            },
            floatingActionButtonPosition = FabPosition.End
        ) {
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .consumeWindowInsets(it)
            ) {
                Body(viewModel, state.value, viewModel.planning) {
                    scope.launch {
                        snackbarHostState.showSnackbar(emptyRecipesMsg)
                    }
                }
            }
        }
    }

    @Composable
    fun UserPhotoIcon(user: User) {
        Surface(
            modifier = Modifier.size(48.dp),
            onClick = {
                val intent = Intent(this@HomePage, ProfilePage::class.java)
                intent.putExtra(HOME_USER, user)
                resultLauncher.launch(intent)
            },
            shape = CircleShape,
            color = Color.Transparent,
            border = BorderStroke(
                1.dp,
                color = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            if (user.photo == Uri.EMPTY) {
                Icon(
                    Icons.Outlined.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                QImage(
                    uri = user.photo,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    @Composable
    fun Body(
        viewModel: HomeViewModel,
        state: UserState,
        planning: List<DayPlanning>,
        onRecipesEmpty: () -> Unit
    ) {

        var showActions by remember { mutableStateOf(false) }
        val day = QDateUtils.getDayOfWeekIndex()

        LaunchedEffect(key1 = Unit, block = {
            delay(500)
            showActions = true
        })

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            QShimmer(controller = state.user != User.EMPTY_USER) {
                Text(
                    stringResource(R.string.home_welcome_message, state.user.name),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = it.padding(start = 64.dp, end = 64.dp, top = 24.dp),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.size(24.dp))
            QContentCard(
                modifier = Modifier.padding(16.dp),
                arrangement = Arrangement.Top,
                alignment = Alignment.CenterHorizontally,
                backgroundContent = { mod -> Icon(Icons.Outlined.Today, "", modifier = mod) }
            ) {
                Text(
                    stringResource(R.string.home_today_food),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.size(6.dp))
                QShimmer(controller = planning.isNotEmpty()) {
                    if (planning.isNotEmpty() && planning[day].lunch == Recipe.EMPTY_RECIPE &&
                        planning[day].dinner == Recipe.EMPTY_RECIPE
                    ) {
                        Text(
                            stringResource(R.string.home_planning_empty_today),
                            style = MaterialTheme.typography.titleSmall,
                            textAlign = TextAlign.Center,
                            modifier = it
                        )
                    } else {
                        Column(
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.Start,
                            modifier = it
                        ) {
                            PlanningTodayItem(
                                if (planning.isEmpty()) {
                                    Recipe.EMPTY_RECIPE
                                } else {
                                    planning[day].lunch
                                }, true
                            )
                            PlanningTodayItem(
                                if (planning.isEmpty()) {
                                    Recipe.EMPTY_RECIPE
                                } else {
                                    planning[day].dinner
                                }, false
                            )
                        }
                    }
                }
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.Top,
                horizontalArrangement = Arrangement.Center,
                content = {
                    items(UserAction.values()) { userAction ->
                        QShimmer(
                            controller = showActions,
                            durationInMillis = 500 * (userAction.ordinal + 1)
                        ) { modifier ->
                            QContentCard(
                                modifier = modifier
                                    .padding(8.dp)
                                    .height(124.dp),
                                onClick = {
                                    when (userAction) {
                                        UserAction.RECIPES -> {
                                            startActivity(
                                                Intent(
                                                    this@HomePage,
                                                    RecipesPage::class.java
                                                )
                                            )
                                        }

                                        UserAction.RANDOM -> {
                                            viewModel.getRandomRecipe(
                                                onSuccess = {
                                                    val intent = Intent(
                                                        this@HomePage,
                                                        RecipeDetailsPage::class.java
                                                    )
                                                    intent.putExtra(RecipeDetailsPage.RECIPE, it)
                                                    startActivity(intent)
                                                },
                                                onEmptyRecipes = {
                                                    onRecipesEmpty()
                                                }
                                            )
                                        }

                                        UserAction.PLANNING -> {
                                            val intent = Intent(
                                                this@HomePage,
                                                PlanningPage::class.java
                                            )
                                            intent.putExtra(HOME_PLANNING, planning.toTypedArray())
                                            resultLauncher.launch(intent)
                                        }

                                        UserAction.SHOPPING -> {
                                            startActivity(
                                                Intent(
                                                    this@HomePage,
                                                    ShoppingListPage::class.java
                                                )
                                            )
                                        }
                                    }
                                },
                                backgroundContent = {
                                    Icon(
                                        userAction.icon,
                                        contentDescription = "",
                                        modifier = it
                                    )
                                }
                            ) {
                                Text(
                                    stringResource(userAction.title),
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                        }
                    }
                }
            )
        }
    }

    @Composable
    fun PlanningTodayItem(recipe: Recipe, isLunch: Boolean) {
        Surface(
            color = Color.Transparent,
            shape = MaterialTheme.shapes.medium,
            onClick = {
                if (recipe != Recipe.EMPTY_RECIPE) {
                    val intent = Intent(this@HomePage, RecipeDetailsPage::class.java)
                    intent.putExtra(RecipeDetailsPage.RECIPE, recipe)
                    intent.putExtra(RecipeDetailsPage.ALLOW_TO_UPDATE, false)
                    startActivity(intent)
                }
            }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.padding(4.dp)
            ) {
                Icon(
                    if (isLunch) {
                        Icons.Outlined.WbSunny
                    } else {
                        Icons.Outlined.NightlightRound
                    }, ""
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    recipe.name.ifEmpty { "-" },
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.size(8.dp))
                Icon(Icons.Outlined.DoubleArrow, "")
            }
        }
    }
}