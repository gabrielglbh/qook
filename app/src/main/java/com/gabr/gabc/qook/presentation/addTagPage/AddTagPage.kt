package com.gabr.gabc.qook.presentation.addTagPage

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
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
        const val HAS_ALTERED_TAG = "HAS_ALTERED_TAG"
        const val HAS_ALTERED_MODE = "HAS_ALTERED_MODE"
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
                    tag = tag,
                    isUpdate = true
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
        val state = viewModel.formState.collectAsState().value

        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }

        var tagError by remember { mutableStateOf(false) }

        fun successCallback(tag: Tag, mode: AlteredMode) {
            val resultIntent = Intent()
            resultIntent.putExtra(HAS_ALTERED_TAG, tag)
            resultIntent.putExtra(HAS_ALTERED_MODE, mode)
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        fun errorCallback(error: String) {
            scope.launch {
                snackbarHostState.showSnackbar(error)
            }
        }

        fun onSuccess() {
            if (state.isUpdate) {
                viewModel.updateTag(
                    state.tag,
                    ifError = { errorCallback(it) },
                    ifSuccess = { successCallback(state.tag, AlteredMode.UPDATE) }
                )
            } else {
                viewModel.createTag(
                    state.tag,
                    ifError = { errorCallback(it) },
                    ifSuccess = { createdTag -> successCallback(createdTag, AlteredMode.CREATE) }
                )
            }
        }

        val saveButton: @Composable () -> Unit = {
            Button(
                onClick = {
                    val text = state.tag.text
                    if (!tagError && text.trim().isNotEmpty()) {
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

        Box {
            Scaffold(
                topBar = {
                    QActionBar(
                        onBack = {
                            finish()
                        },
                        title = if (state.isUpdate) {
                            R.string.tags_modify_title
                        } else {
                            R.string.tags_add_title
                        }
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
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .fillMaxWidth()
                    ) {
                        QTag(tag = state.tag)
                        Spacer(modifier = Modifier.size(12.dp))
                        QTextForm(
                            labelId = R.string.tag_name,
                            value = state.tag.text,
                            onValueChange = { name ->
                                viewModel.updateForm(
                                    state.copy(
                                        tag = state.tag.copy(
                                            text = name
                                        )
                                    )
                                )
                                tagError = Validators.isNameInvalid(name)
                            },
                            leadingIcon = Icons.Outlined.BookmarkBorder,
                        )
                        Spacer(modifier = Modifier.size(12.dp))
                        QColorPicker(
                            modifier = Modifier.weight(1f),
                            initialColor = state.tag.color,
                            selected = { selectedColor ->
                                viewModel.updateForm(
                                    state.copy(
                                        tag = state.tag.copy(
                                            color = selectedColor
                                        )
                                    )
                                )
                            }
                        )
                        Spacer(modifier = Modifier.size(12.dp))
                        if (!state.isUpdate) {
                            saveButton()
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        viewModel.deleteTag(
                                            ifError = { err -> errorCallback(err) },
                                            ifSuccess = {
                                                successCallback(
                                                    state.tag,
                                                    AlteredMode.DELETE
                                                )
                                            }
                                        )
                                    },
                                    border = BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.error
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 12.dp, end = 12.dp, bottom = 8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = Color.Transparent,
                                    )
                                ) {
                                    Text(
                                        stringResource(R.string.tags_remove_tag),
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                                Spacer(modifier = Modifier.size(8.dp))
                                saveButton()
                            }
                        }
                    }
                }
            }
            if (viewModel.isLoading) QLoadingScreen()
        }
    }
}