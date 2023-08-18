package com.gabr.gabc.qook.presentation.homePage

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.ShoppingBasket
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import arrow.core.Either
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.domain.user.User
import com.gabr.gabc.qook.presentation.homePage.viewModel.HomeViewModel
import com.gabr.gabc.qook.presentation.homePage.viewModel.UserState
import com.gabr.gabc.qook.presentation.profilePage.ProfilePage
import com.gabr.gabc.qook.presentation.recipesPage.RecipesPage
import com.gabr.gabc.qook.presentation.shared.components.QActionBar
import com.gabr.gabc.qook.presentation.shared.components.QAutoSizeText
import com.gabr.gabc.qook.presentation.shared.components.QImage
import com.gabr.gabc.qook.presentation.shared.components.QShimmer
import com.gabr.gabc.qook.presentation.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomePage : ComponentActivity() {
    companion object {
        const val HOME_USER = "HOME_USER"
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
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        LaunchedEffect(key1 = Unit) {
            viewModel.getUser { errorMessage ->
                scope.launch {
                    snackbarHostState.showSnackbar(errorMessage)
                }
            }
        }

        Scaffold(
            topBar = {
                QActionBar(
                    actionBehaviour = {
                        val intent = Intent(this@HomePage, ProfilePage::class.java)
                        intent.putExtra(HOME_USER, state.value.user)
                        resultLauncher.launch(intent)
                    },
                    actionBorder = BorderStroke(
                        1.dp,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ),
                    action = Either.Left {
                        if (state.value.user.photo == Uri.EMPTY) {
                            Icon(
                                Icons.Outlined.AccountCircle,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            QImage(
                                uri = state.value.user.photo,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                )
            },
            bottomBar = {
                BottomNavigationBar()
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
                Body(state.value)
            }
        }
    }

    @Composable
    fun Body(
        state: UserState
    ) {
        val configuration = LocalConfiguration.current
        val buttonSize = configuration.screenWidthDp.dp / 1.5f

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            QShimmer(controller = state.user != User.EMPTY_USER) {
                Text(
                    stringResource(R.string.home_welcome_message, state.user.name),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = it.padding(horizontal = 64.dp, vertical = 32.dp),
                    textAlign = TextAlign.Center
                )
            }
            OutlinedButton(
                onClick = { },
                modifier = Modifier
                    .width(buttonSize)
                    .height(buttonSize)
                    .weight(1f, fill = false),
                shape = CircleShape,
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primaryContainer),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(128.dp)
                            .height(128.dp)
                            .padding(bottom = 12.dp),
                    ) {
                        Image(
                            painter = painterResource(R.drawable.random),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer)
                        )
                    }
                    Text(
                        stringResource(R.string.home_get_random_recipe),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .width(buttonSize)
                            .padding(horizontal = 24.dp)
                    )
                }
            }
        }
    }

    @Composable
    fun BottomNavigationBar() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .shadow(
                    elevation = 8.dp,
                    spotColor = Color.Transparent
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            BottomNavButton(
                icon = Icons.Outlined.ShoppingBasket,
                text = stringResource(R.string.home_shopping_bnb),
                onClick = {}
            )
            BottomNavButton(
                icon = Icons.Outlined.Receipt,
                text = stringResource(R.string.home_recipes_bnb),
                onClick = {
                    startActivity(Intent(this@HomePage, RecipesPage::class.java))
                }
            )
            BottomNavButton(
                icon = Icons.Outlined.CalendarMonth,
                text = stringResource(R.string.home_planning_bnb),
                onClick = {}
            )

        }
    }

    @Composable
    fun BottomNavButton(
        onClick: () -> Unit,
        icon: ImageVector,
        text: String
    ) {
        val configuration = LocalConfiguration.current

        Column(
            modifier = Modifier
                .clickable { onClick() }
                .width(configuration.screenWidthDp.dp / 3),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon, contentDescription = null,
                modifier = Modifier
                    .width(32.dp)
                    .height(32.dp),
            )
            QAutoSizeText(
                text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}