package com.gabr.gabc.qook.presentation.loginPage

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.presentation.homePage.HomePage
import com.gabr.gabc.qook.presentation.loginPage.viewModel.LoginFormState
import com.gabr.gabc.qook.presentation.loginPage.viewModel.LoginViewModel
import com.gabr.gabc.qook.presentation.shared.Validators
import com.gabr.gabc.qook.presentation.shared.components.QLoadingScreen
import com.gabr.gabc.qook.presentation.shared.components.QShimmer
import com.gabr.gabc.qook.presentation.shared.components.QTextForm
import com.gabr.gabc.qook.presentation.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class LoginPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                LoginView()
            }
        }
    }

    @Composable
    fun LoginView() {
        val viewModel: LoginViewModel by viewModels()
        val form by viewModel.formState.collectAsState()

        var visibilityText by remember { mutableStateOf(false) }
        var visibilityForm by remember { mutableStateOf(false) }
        val configuration = LocalConfiguration.current

        LaunchedEffect(key1 = Unit, block = {
            delay(500)
            visibilityText = true
            delay(1000)
            visibilityForm = true
        })

        Box {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AnimatedVisibility(
                    visible = visibilityText,
                    enter = slideInVertically(tween(1000)) {
                        configuration.screenHeightDp.dp.value.toInt() / 2
                    }
                ) {
                    Text(
                        getString(R.string.app_name),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.padding(bottom = 48.dp)
                    )
                }
                QShimmer(controller = visibilityForm) {
                    LoginForm(
                        viewModel,
                        form,
                        it.padding(horizontal = 32.dp)
                    )
                }
            }
            if (viewModel.isLoading) QLoadingScreen()
        }
    }

    @Composable
    fun LoginForm(
        viewModel: LoginViewModel,
        form: LoginFormState,
        modifier: Modifier,
    ) {
        val focusManager = LocalFocusManager.current
        val colors = MaterialTheme.colorScheme

        var isRegisterMode by remember { mutableStateOf(false) }
        var errorName by remember { mutableStateOf(false) }
        var errorEmail by remember { mutableStateOf(false) }
        var errorPassword by remember { mutableStateOf(false) }

        val errorForm = stringResource(R.string.error_empty_form)

        fun onSubmit() {
            focusManager.clearFocus()

            if (isRegisterMode) {
                if (errorEmail || errorPassword || errorName || form.name.trim()
                        .isEmpty()
                ) {
                    viewModel.updateLoginState(form.copy(error = errorForm))
                } else {
                    viewModel.createUser {
                        startActivity(Intent(this@LoginPage, HomePage::class.java))
                    }
                }
            } else {
                if (errorEmail || errorPassword) {
                    viewModel.updateLoginState(form.copy(error = errorForm))
                } else {
                    viewModel.signInUser {
                        startActivity(Intent(this@LoginPage, HomePage::class.java))
                    }
                }
            }
        }

        Column(
            modifier = modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AnimatedVisibility(
                visible = isRegisterMode,
                enter = fadeIn(tween(1000)),
                exit = fadeOut(tween(1000))
            ) {
                QTextForm(
                    labelId = R.string.login_name,
                    onValueChange = {
                        viewModel.updateLoginState(form.copy(name = it, error = ""))
                        errorName = Validators.isNameInvalid(form.name)
                    },
                    value = form.name,
                    imeAction = ImeAction.Next,
                    isError = errorName
                )
            }
            QTextForm(
                labelId = R.string.login_email,
                onValueChange = {
                    viewModel.updateLoginState(form.copy(email = it, error = ""))
                    errorEmail = Validators.isEmailInvalid(form.email)
                },
                value = form.email,
                keyboardType = KeyboardType.Email,
                leadingIcon = Icons.Outlined.Email,
                imeAction = ImeAction.Next,
                isError = errorEmail
            )
            QTextForm(
                labelId = R.string.login_password,
                onValueChange = {
                    viewModel.updateLoginState(form.copy(password = it, error = ""))
                    errorPassword = Validators.isPasswordInvalid(form.password)
                },
                value = form.password,
                obscured = true,
                isError = errorPassword,
                onSubmitWithImeAction = { onSubmit() }
            )
            if (form.error.isNotEmpty()) Text(
                form.error,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                onClick = { onSubmit() }
            ) {
                Text(
                    stringResource(
                        if (isRegisterMode) {
                            R.string.register_button
                        } else {
                            R.string.login_button
                        }
                    )
                )
            }
            Text(
                stringResource(
                    if (isRegisterMode) {
                        R.string.register_toggle_2
                    } else {
                        R.string.register_toggle_1
                    }
                ),
                color = colors.onBackground,
                modifier = Modifier
                    .clickable {
                        isRegisterMode = !isRegisterMode
                    }
                    .drawBehind {
                        val strokeWidthPx = 1.dp.toPx()
                        val verticalOffset = size.height - 2.sp.toPx()
                        drawLine(
                            color = colors.onBackground,
                            strokeWidth = strokeWidthPx,
                            start = Offset(0f, verticalOffset),
                            end = Offset(size.width, verticalOffset)
                        )
                    },
            )
        }
    }
}
