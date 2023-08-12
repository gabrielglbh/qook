package com.gabr.gabc.qook.presentation.profilePage

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumedWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.domain.user.User
import com.gabr.gabc.qook.presentation.loginPage.LoginPage
import com.gabr.gabc.qook.presentation.profilePage.components.ProfileRow
import com.gabr.gabc.qook.presentation.profilePage.viewModel.ProfileViewModel
import com.gabr.gabc.qook.presentation.shared.components.QActionBar
import com.gabr.gabc.qook.presentation.shared.components.QImage
import com.gabr.gabc.qook.presentation.shared.components.QShimmer
import com.gabr.gabc.qook.presentation.theme.AppTheme
import com.gabr.gabc.qook.presentation.theme.seed
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfilePage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                ProfileView()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
    @Composable
    fun ProfileView() {
        val viewModel: ProfileViewModel by viewModels()
        val state = viewModel.userState.collectAsState().value

        val snackbarHostState = SnackbarHostState()

        LaunchedEffect(key1 = Unit) {
            viewModel.getUser { errorMessage ->
                CoroutineScope(Dispatchers.Main).launch {
                    snackbarHostState.showSnackbar(errorMessage)
                }
            }
        }

        Scaffold(
            topBar = {
                QActionBar(
                    onBack = {
                        finish()
                    },
                    actionBehaviour = {
                        viewModel.signOut()
                        val intent = Intent(this, LoginPage::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    },
                    action = {
                        Icon(
                            Icons.Outlined.ExitToApp,
                            contentDescription = null
                        )
                    },
                )
            }
        ) {
            Box(
                contentAlignment = if (state.error.isNotEmpty()) {
                    Alignment.Center
                } else {
                    Alignment.TopCenter
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .consumedWindowInsets(it)
            ) {
                if (state.error.isNotEmpty()) {
                    Text(stringResource(R.string.error_user_retrieval))
                } else {
                    Body(state.user)
                }
            }
        }
    }

    @Composable
    fun Body(
        user: User?
    ) {
        val configuration = LocalConfiguration.current
        val buttonSize = configuration.screenWidthDp.dp / 2f

        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedButton(
                onClick = { },
                modifier = Modifier
                    .width(buttonSize)
                    .height(buttonSize),
                shape = CircleShape,
                border = BorderStroke(2.dp, seed),
                contentPadding = PaddingValues(0.dp),
            ) {
                if (user?.avatar == null) {
                    Icon(
                        Icons.Outlined.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                } else {
                    QImage(
                        uri = user.avatar,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            QShimmer(controller = user != null) {
                Text(
                    user?.email ?: "",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = it.padding(top = 12.dp)
                )
            }
            Settings()
            QShimmer(controller = user != null) { modifier ->
                user?.let { u -> Account(u, modifier) }
            }
        }
    }

    @Composable
    fun Settings() {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(top = 32.dp)
        ) {
            Text(
                stringResource(R.string.profile_settings_label),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(start = 16.dp)
            )
            ProfileRow(
                res = R.drawable.theme,
                text = stringResource(R.string.profile_change_app_theme)
            ) {}
            ProfileRow(
                icon = Icons.Outlined.Info,
                text = stringResource(R.string.profile_about_qook)
            ) {}
        }
    }

    @Composable
    fun Account(user: User, modifier: Modifier) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = modifier.padding(top = 24.dp)
        ) {
            Text(
                stringResource(R.string.profile_account_label),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(start = 16.dp)
            )
            ProfileRow(
                icon = Icons.Outlined.Face,
                text = stringResource(R.string.profile_change_name),
                trailingText = user.name
            ) {}
            ProfileRow(
                icon = Icons.Outlined.Lock,
                text = stringResource(R.string.profile_change_password)
            ) {}
            ProfileRow(
                icon = Icons.Outlined.Delete,
                text = stringResource(R.string.profile_delete_account),
                textColor = MaterialTheme.colorScheme.error
            ) {}
        }
    }
}