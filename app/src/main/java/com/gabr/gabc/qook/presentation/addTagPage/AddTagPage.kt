package com.gabr.gabc.qook.presentation.addTagPage

import android.content.Intent
import android.os.Build
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.domain.tag.Tag
import com.gabr.gabc.qook.presentation.addRecipePage.AddRecipePage
import com.gabr.gabc.qook.presentation.addTagPage.viewModel.AddTagViewModel
import com.gabr.gabc.qook.presentation.shared.Validators
import com.gabr.gabc.qook.presentation.shared.components.QActionBar
import com.gabr.gabc.qook.presentation.shared.components.QColorPicker
import com.gabr.gabc.qook.presentation.shared.components.QLoadingScreen
import com.gabr.gabc.qook.presentation.shared.components.QTag
import com.gabr.gabc.qook.presentation.shared.components.QTextForm
import com.gabr.gabc.qook.presentation.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddTagPage : ComponentActivity() {
    companion object {
        const val HAS_CREATED_TAG = "HAS_CREATED_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: AddTagViewModel by viewModels()
        val isUpdate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(AddRecipePage.UPDATE_TAG, Tag::class.java)
        } else {
            intent.getParcelableExtra(AddRecipePage.UPDATE_TAG)
        }
        isUpdate?.let { tag ->
            viewModel.updateForm(
                viewModel.formState.value.copy(
                    previousTag = tag,
                    tag = tag
                )
            )
        }

        setContent {
            AppTheme {
                AddTagView()
            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun AddTagView() {
        val viewModel: AddTagViewModel by viewModels()
        val state = viewModel.formState.collectAsState()
        val focusManager = LocalFocusManager.current

        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }

        var tagError by remember { mutableStateOf(false) }

        fun onSuccess() {
            if (state.value.previousTag != null) {
                viewModel.updateTag(
                    state.value.tag,
                    ifError = {
                        scope.launch {
                            snackbarHostState.showSnackbar(it)
                        }
                    },
                    ifSuccess = {
                        val resultIntent = Intent()
                        resultIntent.putExtra(HAS_CREATED_TAG, true)
                        setResult(RESULT_OK, resultIntent)
                        finish()
                    }
                )
            } else {
                viewModel.createTag(
                    state.value.tag,
                    ifError = {
                        scope.launch {
                            snackbarHostState.showSnackbar(it)
                        }
                    },
                    ifSuccess = {
                        val resultIntent = Intent()
                        resultIntent.putExtra(HAS_CREATED_TAG, true)
                        setResult(RESULT_OK, resultIntent)
                        finish()
                    }
                )
            }
        }

        Box {
            Scaffold(
                topBar = {
                    QActionBar(
                        onBack = {
                            finish()
                        },
                        title = R.string.tags_add_title
                    )
                },
                snackbarHost = {
                    SnackbarHost(snackbarHostState)
                }
            ) {
                Box(
                    modifier = Modifier
                        .padding(it)
                        .consumeWindowInsets(it)
                ) {
                    Column(
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            QTag(tag = state.value.tag, modifier = Modifier.scale(2f))
                            Spacer(modifier = Modifier.size(12.dp))
                            QTextForm(
                                labelId = R.string.tag_name,
                                value = state.value.tag.text,
                                onValueChange = { name ->
                                    viewModel.updateForm(
                                        state.value.copy(
                                            tag = state.value.tag.copy(
                                                text = name
                                            )
                                        )
                                    )
                                    tagError = Validators.isNameInvalid(name)
                                },
                                leadingIcon = Icons.Outlined.BookmarkBorder,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                            Spacer(modifier = Modifier.size(12.dp))
                            QColorPicker(
                                modifier = Modifier.weight(1f)
                            ) { selectedColor ->
                                focusManager.clearFocus()
                                viewModel.updateForm(
                                    state.value.copy(
                                        tag = state.value.tag.copy(
                                            color = selectedColor
                                        )
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.size(12.dp))
                            Button(
                                onClick = {
                                    val text = state.value.tag.text
                                    val color = state.value.tag.color
                                    if (!tagError && text.trim()
                                            .isNotEmpty() && color != Color.Transparent
                                    ) {
                                        onSuccess()
                                    } else {
                                        if (tagError || text.trim().isEmpty()) tagError = true
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 12.dp, end = 12.dp, bottom = 8.dp)
                            ) {
                                Text(stringResource(R.string.tag_save_title))
                            }
                        }
                    }
                }
            }
            if (viewModel.isLoading) QLoadingScreen()
        }
    }
}