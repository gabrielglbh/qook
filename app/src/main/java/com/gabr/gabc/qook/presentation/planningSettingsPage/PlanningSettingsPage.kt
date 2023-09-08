package com.gabr.gabc.qook.presentation.planningSettingsPage

import android.Manifest
import android.content.Intent
import android.os.Build
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.domain.sharedPlanning.SharedPlanning
import com.gabr.gabc.qook.presentation.planningSettingsPage.viewModel.PlanningSettingsViewModel
import com.gabr.gabc.qook.presentation.shared.IntentVars.Companion.HAS_REMOVED_SHARED_PLANNING
import com.gabr.gabc.qook.presentation.shared.IntentVars.Companion.HAS_UPDATED_SHARED_PLANNING
import com.gabr.gabc.qook.presentation.shared.IntentVars.Companion.SHARED_PLANNING
import com.gabr.gabc.qook.presentation.shared.PermissionsRequester
import com.gabr.gabc.qook.presentation.shared.QDateUtils
import com.gabr.gabc.qook.presentation.shared.components.QActionBar
import com.gabr.gabc.qook.presentation.shared.components.QChangeNameDialog
import com.gabr.gabc.qook.presentation.shared.components.QChangeWeekBeginningBottomSheet
import com.gabr.gabc.qook.presentation.shared.components.QContentCard
import com.gabr.gabc.qook.presentation.shared.components.QDialog
import com.gabr.gabc.qook.presentation.shared.components.QImageContainer
import com.gabr.gabc.qook.presentation.shared.components.QLoadingScreen
import com.gabr.gabc.qook.presentation.shared.components.QSelectableItem
import com.gabr.gabc.qook.presentation.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlanningSettingsPage : ComponentActivity() {
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var requestMultiplePermissions: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: PlanningSettingsViewModel by viewModels()
        pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    viewModel.updateGroupPhoto(uri)
                }
            }

        requestMultiplePermissions =
            PermissionsRequester.requestMultiplePermissionsCaller(this, pickMedia)

        setContent {
            AppTheme {
                PlanningSettingsView()
            }
        }
    }

    @Composable
    fun PlanningSettingsView() {
        val viewModel: PlanningSettingsViewModel by viewModels()

        LaunchedEffect(key1 = Unit, block = {
            val loadedSharedPlanning = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(
                    SHARED_PLANNING,
                    SharedPlanning::class.java
                )
            } else {
                intent.getParcelableExtra(SHARED_PLANNING)
            }

            loadedSharedPlanning?.let {
                viewModel.loadSharedPlanning(it)
            }
        })

        Box {
            Scaffold(
                topBar = {
                    QActionBar(
                        title = R.string.planning_settings_title,
                        onBack = {
                            val resultIntent = Intent()
                            resultIntent.putExtra(
                                HAS_UPDATED_SHARED_PLANNING,
                                viewModel.sharedPlanning.value
                            )
                            setResult(RESULT_OK, resultIntent)
                            finish()
                        },
                    )
                }
            ) {
                Box(
                    contentAlignment = Alignment.TopCenter,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                        .consumeWindowInsets(it),
                ) {
                    Settings()
                }
            }
            if (viewModel.isLoading.value) QLoadingScreen()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Settings() {
        val viewModel: PlanningSettingsViewModel by viewModels()
        val group = viewModel.sharedPlanning.value

        var showNameDialog by remember { mutableStateOf(false) }
        var showWeekBeginningBottomSheet by remember { mutableStateOf(false) }
        var showRemoveSharedPlanningDialog by remember { mutableStateOf(false) }
        val sheetState = rememberModalBottomSheetState()

        if (showNameDialog)
            QChangeNameDialog(
                setShowDialog = {
                    showNameDialog = it
                },
                icon = Icons.Outlined.Group,
                onClick = {
                    viewModel.updateSharedPlanning(group.copy(name = it))
                }
            )

        if (showWeekBeginningBottomSheet) {
            QChangeWeekBeginningBottomSheet(
                selected = group.resetDay,
                modalBottomSheetState = sheetState,
                setShowDialog = {
                    showWeekBeginningBottomSheet = it
                },
                list = QDateUtils.days.map { stringResource(it) },
                onClick = {
                    viewModel.updateSharedPlanning(group.copy(resetDay = it))
                }
            )
        }

        if (showRemoveSharedPlanningDialog) {
            QDialog(
                onDismissRequest = { showRemoveSharedPlanningDialog = false },
                leadingIcon = Icons.Outlined.Delete,
                title = R.string.plannings_delete_group_title,
                buttonTitle = R.string.plannings_remove,
                content = {
                    Text(
                        stringResource(R.string.plannings_delete_group_description),
                        color = MaterialTheme.colorScheme.error
                    )
                },
                onSubmit = {
                    viewModel.deleteSharedPlanning {
                        val resultIntent = Intent()
                        resultIntent.putExtra(HAS_REMOVED_SHARED_PLANNING, true)
                        setResult(RESULT_OK, resultIntent)
                        finish()
                    }
                    showRemoveSharedPlanningDialog = false
                },
            )
        }

        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            QImageContainer(
                uri = group.photo,
                placeholder = Icons.Outlined.Group,
                onClick = {
                    requestMultiplePermissions.launch(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
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
            )
            Spacer(modifier = Modifier.size(8.dp))
            QContentCard(
                modifier = Modifier.padding(12.dp),
                arrangement = Arrangement.Top,
                alignment = Alignment.Start,
                backgroundContent = { m ->
                    Icon(Icons.Outlined.Group, "", modifier = m)
                }
            ) {
                Text(
                    stringResource(R.string.planning_group_info),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
                )
                QSelectableItem(
                    icon = Icons.Outlined.Group,
                    text = stringResource(R.string.profile_change_name),
                    trailingText = group.name
                ) {
                    showNameDialog = true
                }
                QSelectableItem(
                    icon = Icons.Outlined.CalendarMonth,
                    text = stringResource(R.string.profile_change_reset_timing),
                    trailingText = stringResource(QDateUtils.days[group.resetDay])
                ) {
                    showWeekBeginningBottomSheet = true
                }
                QSelectableItem(
                    icon = Icons.Outlined.Delete,
                    text = stringResource(R.string.plannings_remove_shared_planning),
                    textColor = MaterialTheme.colorScheme.error,
                ) {
                    showRemoveSharedPlanningDialog = true
                }
            }
        }
    }
}