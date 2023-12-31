package com.gabr.gabc.qook.presentation.addRecipePage.components

import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.domain.recipe.Easiness
import com.gabr.gabc.qook.presentation.addRecipePage.viewModel.AddRecipeViewModel
import com.gabr.gabc.qook.presentation.shared.Validators
import com.gabr.gabc.qook.presentation.shared.components.QImageContainer
import com.gabr.gabc.qook.presentation.shared.components.QPhotoDialog
import com.gabr.gabc.qook.presentation.shared.components.QTextForm
import com.gabr.gabc.qook.presentation.shared.components.QTextTitle

@Composable
fun RecipeMetadataForm(
    modifier: Modifier,
    onNavigate: () -> Unit,
    requestMultiplePermissions: ActivityResultLauncher<Array<String>>,
    viewModel: AddRecipeViewModel
) {
    val state = viewModel.recipeState.collectAsState().value
    val focusManager = LocalFocusManager.current

    var nameFieldError by remember { mutableStateOf(false) }
    var timeFieldError by remember { mutableStateOf(false) }
    var photoOptions by remember { mutableStateOf(false) }

    if (photoOptions) {
        QPhotoDialog(requestMultiplePermissions, focusManager) {
            photoOptions = false
        }
    }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        QTextTitle(
            title = R.string.add_recipe_provide_the_basics,
            subtitle = R.string.add_recipe_basics_navigation
        )
        Spacer(modifier = Modifier.size(8.dp))
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(1f),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                contentAlignment = Alignment.BottomEnd
            ) {
                QImageContainer(
                    uri = state.recipe.photo,
                    placeholder = Icons.Outlined.AddAPhoto,
                ) {
                    photoOptions = true
                }
                IconButton(
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    ),
                    onClick = {
                        viewModel.emptyPhoto()
                    }
                ) {
                    Icon(Icons.Outlined.Clear, "")
                }
            }
            Spacer(modifier = Modifier.size(20.dp))
            QTextForm(
                labelId = R.string.add_recipe_name,
                value = state.recipe.name,
                isError = nameFieldError,
                leadingIcon = Icons.AutoMirrored.Outlined.ReceiptLong,
                imeAction = ImeAction.Next,
                onValueChange = {
                    viewModel.updateMetadata(name = it)
                    nameFieldError = Validators.isRecipeNameInvalid(it)
                }
            )
            Spacer(modifier = Modifier.size(20.dp))
            QTextForm(
                labelId = R.string.add_recipe_time,
                value = state.recipe.time,
                isError = timeFieldError,
                leadingIcon = Icons.Outlined.Timer,
                onValueChange = {
                    viewModel.updateMetadata(time = it)
                    timeFieldError = Validators.isNameInvalid(it)
                },
            )
            Spacer(modifier = Modifier.size(20.dp))
            EasinessComponent(state.recipe.easiness, onSelect = {
                focusManager.clearFocus()
                viewModel.updateMetadata(easiness = it)
            })
        }
        Button(
            onClick = {
                if (state.recipe.name.trim()
                        .isNotEmpty() && !nameFieldError && state.recipe.time.trim()
                        .isNotEmpty() && !timeFieldError
                ) {
                    onNavigate()
                } else {
                    nameFieldError = Validators.isNameInvalid(state.recipe.name)
                    timeFieldError = Validators.isNameInvalid(state.recipe.time)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text(stringResource(R.string.add_recipe_next))
        }
    }
}

@Composable
fun EasinessComponent(
    selected: Easiness,
    onSelect: (Easiness) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            stringResource(R.string.add_recipe_easiness),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.size(12.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Easiness.values().forEach { easiness ->
                Surface(
                    shape = MaterialTheme.shapes.small,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .weight(1f),
                    color = if (easiness == selected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.background
                    },
                    onClick = {
                        onSelect(easiness)
                    },
                ) {
                    Text(
                        when (easiness) {
                            Easiness.EASY -> {
                                stringResource(R.string.add_recipe_easiness_EASY)
                            }

                            Easiness.MEDIUM -> {
                                stringResource(
                                    R.string.add_recipe_easiness_MEDIUM
                                )
                            }

                            else -> {
                                stringResource(
                                    R.string.add_recipe_easiness_HARD
                                )
                            }
                        },
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(8.dp),
                    )
                }
            }
        }
    }
}