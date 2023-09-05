package com.gabr.gabc.qook.presentation.sharedPlanningPage

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.presentation.shared.components.QActionBar
import com.gabr.gabc.qook.presentation.shared.components.QTextTitle
import com.gabr.gabc.qook.presentation.sharedPlanningPage.viewModel.SharedPlanningViewModel
import com.gabr.gabc.qook.presentation.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SharedPlanningPage : ComponentActivity() {
    companion object {
        const val GROUP_ID = "GROUP_ID"
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

        Scaffold(
            topBar = {
                QActionBar(
                    title = R.string.shared_planning_create_title,
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
                Column(
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(12.dp)
                ) {
                    QTextTitle(
                        rawTitle = group.name,
                        subtitle = R.string.shared_planning_info_display
                    )
                    Spacer(modifier = Modifier.size(12.dp))
                }
            }
        }
    }
}