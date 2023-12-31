package com.gabr.gabc.qook.presentation.addSharedPlanningPage

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.presentation.addSharedPlanningPage.viewModel.AddSharedPlanningViewModel
import com.gabr.gabc.qook.presentation.planningPage.PlanningPage
import com.gabr.gabc.qook.presentation.shared.IntentVars.Companion.SHARED_PLANNING_ID
import com.gabr.gabc.qook.presentation.shared.PermissionsRequester
import com.gabr.gabc.qook.presentation.shared.QDateUtils
import com.gabr.gabc.qook.presentation.shared.Validators
import com.gabr.gabc.qook.presentation.shared.components.QActionBar
import com.gabr.gabc.qook.presentation.shared.components.QChangeWeekBeginningBottomSheet
import com.gabr.gabc.qook.presentation.shared.components.QImageContainer
import com.gabr.gabc.qook.presentation.shared.components.QLoadingScreen
import com.gabr.gabc.qook.presentation.shared.components.QPhotoDialog
import com.gabr.gabc.qook.presentation.shared.components.QSelectableItem
import com.gabr.gabc.qook.presentation.shared.components.QTextForm
import com.gabr.gabc.qook.presentation.shared.components.QTextTitle
import com.gabr.gabc.qook.presentation.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddSharedPlanningPage : ComponentActivity() {
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var photoMedia: ActivityResultLauncher<Uri>
    private lateinit var requestMultiplePermissions: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: AddSharedPlanningViewModel by viewModels()

        val photoUri = PermissionsRequester.getPhotoUri(this)
        pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    viewModel.updateSharedPlanning(photo = uri)
                }
            }
        photoMedia = registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) {
                viewModel.updateSharedPlanning(photo = photoUri)
            }
        }

        requestMultiplePermissions =
            PermissionsRequester.requestMultiplePermissionsCaller(
                this,
                pickMedia,
                photoMedia,
                photoUri
            )

        setContent {
            AppTheme {
                AddSharedPlanningView()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AddSharedPlanningView() {
        val viewModel: AddSharedPlanningViewModel by viewModels()
        val sharedPlanning = viewModel.sharedPlanning.value

        val focusManager = LocalFocusManager.current

        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }

        var nameError by remember { mutableStateOf(false) }

        var showWeekBeginningBottomSheet by remember { mutableStateOf(false) }
        val sheetState = rememberModalBottomSheetState()

        if (showWeekBeginningBottomSheet) {
            QChangeWeekBeginningBottomSheet(
                selected = sharedPlanning.resetDay,
                modalBottomSheetState = sheetState,
                setShowDialog = {
                    showWeekBeginningBottomSheet = it
                },
                list = QDateUtils.days.map { stringResource(it) },
                onClick = {
                    viewModel.updateSharedPlanning(resetDay = it)
                }
            )
        }

        var photoOptions by remember { mutableStateOf(false) }
        if (photoOptions) {
            QPhotoDialog(requestMultiplePermissions, focusManager) {
                photoOptions = false
            }
        }

        Box {
            Scaffold(
                topBar = {
                    QActionBar(
                        title = R.string.shared_planning_create_title,
                        onBack = {
                            finish()
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
                        modifier = Modifier.padding(12.dp)
                    ) {
                        QTextTitle(
                            title = R.string.add_recipe_provide_the_basics,
                            subtitle = R.string.shared_planning_create_info
                        )
                        Spacer(modifier = Modifier.size(12.dp))
                        QImageContainer(
                            uri = sharedPlanning.photo,
                            placeholder = Icons.Outlined.AddAPhoto,
                        ) {
                            photoOptions = true
                        }
                        Spacer(modifier = Modifier.size(12.dp))
                        QTextForm(
                            labelId = R.string.login_name,
                            value = sharedPlanning.name,
                            isError = nameError,
                            leadingIcon = Icons.Outlined.Group,
                            imeAction = ImeAction.Next,
                            onValueChange = { v ->
                                viewModel.updateSharedPlanning(name = v)
                                nameError = Validators.isRecipeNameInvalid(v)
                            }
                        )
                        Spacer(modifier = Modifier.size(32.dp))
                        QSelectableItem(
                            icon = Icons.Outlined.CalendarMonth,
                            text = stringResource(R.string.profile_change_reset_timing),
                            trailingText = stringResource(QDateUtils.days[sharedPlanning.resetDay])
                        ) {
                            showWeekBeginningBottomSheet = true
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Button(
                            onClick = {
                                if (sharedPlanning.name.trim().isNotEmpty() && !nameError
                                ) {
                                    viewModel.createSharedPlanning(
                                        onError = { e ->
                                            scope.launch {
                                                snackbarHostState.showSnackbar(e)
                                            }
                                        },
                                        onSuccess = { sharedPlanningId ->
                                            val intent = Intent(
                                                this@AddSharedPlanningPage,
                                                PlanningPage::class.java
                                            )
                                            intent.putExtra(
                                                SHARED_PLANNING_ID,
                                                sharedPlanningId
                                            )
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                            startActivity(intent)
                                            finish()
                                        }
                                    )
                                } else {
                                    nameError = Validators.isRecipeNameInvalid(sharedPlanning.name)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            Text(
                                stringResource(R.string.shared_planning_create_shared_planning),
                            )
                        }
                    }
                }
            }
            if (viewModel.isLoading.value) QLoadingScreen()
        }
    }
}