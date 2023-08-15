package com.gabr.gabc.qook.presentation.addRecipePage.components

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.domain.recipe.Easiness
import com.gabr.gabc.qook.presentation.addRecipePage.viewModel.AddRecipeViewModel
import com.gabr.gabc.qook.presentation.shared.Validators
import com.gabr.gabc.qook.presentation.shared.components.QImage
import com.gabr.gabc.qook.presentation.shared.components.QTextForm

@Composable
fun RecipeMetadataForm(
    onNavigate: () -> Unit,
    requestMultiplePermissions: ActivityResultLauncher<Array<String>>,
    viewModel: AddRecipeViewModel
) {
    val state = viewModel.recipeState.collectAsState().value
    val focusManager = LocalFocusManager.current
    val configuration = LocalConfiguration.current
    val buttonSize = configuration.screenWidthDp.dp / 2f

    var nameFieldError by remember { mutableStateOf(false) }
    var timeFieldError by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(1f),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OutlinedButton(
                onClick = {
                    focusManager.clearFocus()
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
                },
                modifier = Modifier
                    .width(buttonSize)
                    .height(buttonSize),
                shape = CircleShape,
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                contentPadding = PaddingValues(0.dp),
            ) {
                if (state.recipe.photo == Uri.EMPTY) {
                    Icon(
                        Icons.Outlined.AddAPhoto,
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.CenterVertically),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                } else {
                    QImage(
                        uri = state.recipe.photo,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
            Spacer(modifier = Modifier.size(20.dp))
            QTextForm(
                labelId = R.string.add_recipe_name,
                value = state.recipe.name,
                isError = nameFieldError,
                leadingIcon = Icons.Outlined.ReceiptLong,
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
                }
            )
            Spacer(modifier = Modifier.size(20.dp))
            EasinessComponent(state.recipe.easiness, onSelect = {
                focusManager.clearFocus()
                viewModel.updateMetadata(easiness = it)
            })
        }
        Button(
            onClick = {
                if (state.recipe.name.trim().isNotEmpty() && state.recipe.time.trim()
                        .isNotEmpty()
                ) {
                    onNavigate()
                } else {
                    if (state.recipe.name.trim().isEmpty()) nameFieldError = true
                    if (state.recipe.time.trim().isEmpty()) timeFieldError = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 32.dp, end = 32.dp)
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
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
            )
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
                        MaterialTheme.colorScheme.primary
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