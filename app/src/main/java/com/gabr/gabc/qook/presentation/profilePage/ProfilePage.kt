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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import com.gabr.gabc.qook.presentation.profilePage.components.Account
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

        pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    val viewModel: ProfileViewModel by viewModels()
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
            viewModel.getAvatar { errorMessage ->
                CoroutineScope(Dispatchers.Main).launch {
                    snackbarHostState.showSnackbar(errorMessage)
                }
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
                    .consumeWindowInsets(it)
            ) {
                if (state.error.isNotEmpty()) {
                    Text(stringResource(R.string.error_user_retrieval))
                } else {
                    Body(state.user, state.avatarUrl)
                }
            }
        }
    }

    @Composable
    fun Body(
        user: User?,
        avatarUrl: Uri,
    ) {
        val configuration = LocalConfiguration.current
        val buttonSize = configuration.screenWidthDp.dp / 2f

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
                if (avatarUrl == Uri.EMPTY) {
                    Icon(
                        Icons.Outlined.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                } else {
                    QImage(
                        uri = avatarUrl,
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
                val viewModel: ProfileViewModel by viewModels()
                user?.let { u ->
                    Account(u, modifier, viewModel) {
                        hasChangedName = true
                    }
                }
            }
        }
    }

    @Composable
    fun Settings() {
        Surface(
            modifier = Modifier.padding(vertical = 24.dp, horizontal = 12.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    stringResource(R.string.profile_settings_label),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    modifier = Modifier.padding(start = 16.dp, top = 12.dp)
                )
                ProfileRow(
                    res = R.drawable.theme,
                    text = stringResource(R.string.profile_change_app_theme)
                ) {}
                ProfileRow(
                    icon = Icons.Outlined.Info,
                    text = stringResource(R.string.profile_about_qook),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {}
            }
        }
    }
}