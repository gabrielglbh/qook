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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import com.gabr.gabc.qook.domain.user.User
import com.gabr.gabc.qook.presentation.homePage.viewModel.HomeViewModel
import com.gabr.gabc.qook.presentation.homePage.viewModel.UserState
import com.gabr.gabc.qook.presentation.profilePage.ProfilePage
import com.gabr.gabc.qook.presentation.recipesPage.RecipesPage
import com.gabr.gabc.qook.presentation.shared.components.QActionBar
import com.gabr.gabc.qook.presentation.shared.components.QContentCard
import com.gabr.gabc.qook.presentation.shared.components.QImage
import com.gabr.gabc.qook.presentation.shared.components.QShimmer
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
                    actions = listOf {
                        UserPhotoIcon(state.value.user)
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
                Body(state.value)
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
        state: UserState
    ) {
        var showActions by remember { mutableStateOf(false) }

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
                    modifier = it.padding(horizontal = 64.dp, vertical = 32.dp),
                    textAlign = TextAlign.Center
                )
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
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

                                        UserAction.RANDOM -> {}
                                        UserAction.PLANNING -> {}
                                        UserAction.SHOPPING -> {}
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
}