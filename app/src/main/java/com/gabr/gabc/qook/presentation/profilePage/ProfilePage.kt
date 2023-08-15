package com.gabr.gabc.qook.presentation.profilePage

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ExitToApp
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
import androidx.compose.ui.platform.LocalConfiguration
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
import com.gabr.gabc.qook.presentation.shared.components.QImage
import com.gabr.gabc.qook.presentation.shared.components.QShimmer
import com.gabr.gabc.qook.presentation.theme.AppTheme
import com.gabr.gabc.qook.presentation.theme.seed
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfilePage : ComponentActivity() {
    companion object {
        const val HAS_CHANGED_PROFILE_PICTURE = "HAS_CHANGED_PROFILE_PICTURE"
        const val HAS_CHANGED_NAME = "HAS_CHANGED_NAME"
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
        val avatar = if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(HomePage.HOME_USER_AVATAR, Uri::class.java)
        } else {
            intent.getParcelableExtra(HomePage.HOME_USER_AVATAR)
        } ?: Uri.EMPTY

        viewModel.setDataForLocalLoading(user, avatar)

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

        val configuration = LocalConfiguration.current
        val buttonSize = configuration.screenWidthDp.dp / 2f

        val passwordChangeSuccessfulMessage =
            stringResource(R.string.profile_password_change_successful)

        if (state.user == null || state.avatarUrl == Uri.EMPTY) {
            LaunchedEffect(key1 = Unit) {
                viewModel.getUser { errorMessage ->
                    scope.launch {
                        snackbarHostState.showSnackbar(errorMessage)
                    }
                }
                viewModel.getAvatar()
            }
        }

        Scaffold(
            topBar = {
                QActionBar(
                    onBack = {
                        val resultIntent = Intent()
                        resultIntent.putExtra(HAS_CHANGED_PROFILE_PICTURE, hasChangedProfilePicture)
                        resultIntent.putExtra(HAS_CHANGED_NAME, hasChangedName)
                        setResult(RESULT_OK, resultIntent)
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
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
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
                        OutlinedButton(
                            onClick = {
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
                            },
                            modifier = Modifier
                                .width(buttonSize)
                                .height(buttonSize),
                            shape = CircleShape,
                            border = BorderStroke(2.dp, seed),
                            contentPadding = PaddingValues(0.dp),
                        ) {
                            if (state.avatarUrl == Uri.EMPTY) {
                                Icon(
                                    Icons.Outlined.AccountCircle,
                                    contentDescription = null,
                                    modifier = Modifier.align(Alignment.CenterVertically),
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            } else {
                                QImage(
                                    uri = state.avatarUrl,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                        QShimmer(controller = state.user != null) { modifier ->
                            Text(
                                state.user?.email ?: "",
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = modifier.padding(top = 12.dp)
                            )
                        }
                        QShimmer(controller = state.user != null) { modifier ->
                            state.user?.let { u -> Settings(viewModel, u, modifier) }
                        }
                        QShimmer(controller = state.user != null) { modifier ->
                            state.user?.let { u ->
                                Account(viewModel, u, modifier,
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
}