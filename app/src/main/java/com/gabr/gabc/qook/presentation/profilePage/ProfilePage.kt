package com.gabr.gabc.qook.presentation.profilePage

import android.Manifest
import android.content.Intent
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
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
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.domain.user.User
import com.gabr.gabc.qook.presentation.homePage.HomePage
import com.gabr.gabc.qook.presentation.loginPage.LoginPage
import com.gabr.gabc.qook.presentation.profilePage.components.Account
import com.gabr.gabc.qook.presentation.profilePage.components.Settings
import com.gabr.gabc.qook.presentation.profilePage.viewModel.ProfileViewModel
import com.gabr.gabc.qook.presentation.shared.components.QActionBar
import com.gabr.gabc.qook.presentation.shared.components.QAutoSizeText
import com.gabr.gabc.qook.presentation.shared.components.QImageContainer
import com.gabr.gabc.qook.presentation.shared.components.QShimmer
import com.gabr.gabc.qook.presentation.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfilePage : ComponentActivity() {
    companion object {
        const val HAS_UPDATED_PROFILE = "HAS_UPDATED_PROFILE"
    }

    private var hasChangedProfilePicture = false
    private var hasChangedName = false

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
                        if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: ProfileViewModel by viewModels()
        val user = if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(HomePage.HOME_USER, User::class.java)
        } else {
            intent.getParcelableExtra(HomePage.HOME_USER)
        }

        viewModel.setDataForLocalLoading(user)

        pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    viewModel.updateAvatar(uri)
                    hasChangedProfilePicture = true
                }
            }

        setContent {
            AppTheme {
                ProfileView()
            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun ProfileView() {
        val viewModel: ProfileViewModel by viewModels()
        val state = viewModel.userState.collectAsState().value

        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }

        val isUserEmpty = state.user == User.EMPTY_USER

        val passwordChangeSuccessfulMessage =
            stringResource(R.string.profile_password_change_successful)

        if (isUserEmpty) {
            LaunchedEffect(key1 = Unit) {
                viewModel.getUser { errorMessage ->
                    scope.launch {
                        snackbarHostState.showSnackbar(errorMessage)
                    }
                }
            }
        }

        Scaffold(
            topBar = {
                QActionBar(
                    onBack = {
                        val resultIntent = Intent()
                        resultIntent.putExtra(
                            HAS_UPDATED_PROFILE,
                            hasChangedName || hasChangedProfilePicture
                        )
                        setResult(RESULT_OK, resultIntent)
                        finish()
                    },
                    actions = listOf {
                        IconButton(
                            onClick = {
                                viewModel.signOut()
                                val intent = Intent(this, LoginPage::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            }
                        ) {
                            Icon(Icons.Outlined.ExitToApp, "")
                        }
                    }
                )
            },
            snackbarHost = {
                SnackbarHost(snackbarHostState)
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
                    .consumeWindowInsets(it)
            ) {
                if (state.error.isNotEmpty()) {
                    Text(stringResource(R.string.error_user_retrieval))
                } else {
                    Column(
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        QImageContainer(
                            uri = state.user.photo,
                            placeholder = Icons.Outlined.AccountCircle,
                        ) {
                            requestMultiplePermissions.launch(
                                if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
                                    arrayOf(
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        Manifest.permission.READ_MEDIA_IMAGES
                                    )
                                } else {
                                    arrayOf(
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        Manifest.permission.READ_EXTERNAL_STORAGE
                                    )
                                }
                            )
                        }
                        QShimmer(controller = !isUserEmpty) { modifier ->
                            QAutoSizeText(
                                text = state.user.email,
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = modifier.padding(top = 12.dp)
                            )
                        }
                        QShimmer(controller = !isUserEmpty) { modifier ->
                            Settings(viewModel, state.user, modifier)
                        }
                        QShimmer(controller = !isUserEmpty) { modifier ->
                            Account(viewModel, state.user, modifier,
                                onNameUpdated = {
                                    hasChangedName = true
                                },
                                onChangePasswordSuccess = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            passwordChangeSuccessfulMessage
                                        )
                                    }
                                },
                                onChangePasswordError = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(it)
                                    }
                                },
                                onDeleteAccountSuccess = {
                                    val intent = Intent(this@ProfilePage, LoginPage::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                },
                                onDeleteAccountError = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(it)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}