package com.gabr.gabc.qook.presentation.planningsPage

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.presentation.addSharedPlanningPage.AddSharedPlanningPage
import com.gabr.gabc.qook.presentation.planningsPage.viewModel.PlanningsViewModel
import com.gabr.gabc.qook.presentation.shared.components.QActionBar
import com.gabr.gabc.qook.presentation.shared.components.QEmptyBox
import com.gabr.gabc.qook.presentation.shared.components.QLoadingScreen
import com.gabr.gabc.qook.presentation.shared.components.QShimmer
import com.gabr.gabc.qook.presentation.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlanningsPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                PlanningsView()
            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun PlanningsView() {
        val viewModel: PlanningsViewModel by viewModels()
        val groups = viewModel.groups.toList()

        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(key1 = Unit, block = {
            viewModel.loadGroups { error ->
                scope.launch {
                    snackbarHostState.showSnackbar(error)
                }
            }
        })

        Box {
            Scaffold(
                topBar = {
                    QActionBar(
                        title = R.string.shared_plannings_title,
                        onBack = {
                            finish()
                        },
                        actions = listOf {
                            IconButton(
                                onClick = {
                                    startActivity(
                                        Intent(
                                            this@PlanningsPage,
                                            AddSharedPlanningPage::class.java
                                        )
                                    )
                                }
                            ) {
                                Icon(Icons.Outlined.Add, "")
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
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            stringResource(R.string.shared_plannings_info),
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = MaterialTheme.colorScheme.outline
                            ),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        QShimmer(controller = groups.isNotEmpty()) { modifier ->
                            LazyColumn(
                                modifier = modifier,
                                content = {
                                    items(groups) { group ->
                                        Surface(
                                            shape = MaterialTheme.shapes.small,
                                            color = Color.Transparent,
                                            onClick = {
                                                // TODO: Go to shared planning
                                            }
                                        ) {
                                            Text(
                                                group.name,
                                                style = MaterialTheme.typography.titleMedium,
                                            )
                                        }
                                    }
                                }
                            )
                        }
                        if (groups.isEmpty()) QEmptyBox(
                            message = R.string.shared_plannings_empty,
                            icon = Icons.Outlined.Group,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            if (viewModel.isLoading.value) QLoadingScreen()
        }
    }
}