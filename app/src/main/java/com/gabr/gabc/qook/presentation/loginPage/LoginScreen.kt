package com.gabr.gabc.qook.presentation.loginPage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.presentation.loginPage.viewModel.LoginState
import com.gabr.gabc.qook.presentation.loginPage.viewModel.LoginViewModel
import com.gabr.gabc.qook.presentation.shared.QLoadingScreen
import com.gabr.gabc.qook.presentation.shared.QTextForm
import com.gabr.gabc.qook.presentation.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                LoginScreenView()
            }
        }
    }

    @Composable
    fun LoginScreenView() {
        val viewModel: LoginViewModel by viewModels()
        val state by viewModel.loginState.collectAsState()

        var visibilityText by remember { mutableStateOf(false) }
        var visibilityForm by remember { mutableStateOf(false) }
        val configuration = LocalConfiguration.current

        LaunchedEffect(key1 = Unit, block = {
            delay(500)
            visibilityText = true
            delay(1000)
            visibilityForm = true
        })

        val alphaForm by animateFloatAsState(if (visibilityForm) { 1f } else { 0f }, tween(1000),
            label = "alphaForm"
        )

        return Box {
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
                            color = MaterialTheme.colorScheme.inversePrimary
                        ),
                        modifier = Modifier.padding(bottom = 48.dp)
                    )
                }
                LoginForm(
                    viewModel,
                    state,
                    Modifier.padding(horizontal = 32.dp).alpha(alphaForm)
                )
            }
            if (viewModel.isSigningIn) QLoadingScreen()
        }
    }

    @Composable
    fun LoginForm(
        viewModel: LoginViewModel,
        state: LoginState,
        modifier: Modifier,
    ) {
        val focusManager = LocalFocusManager.current
        val colors = MaterialTheme.colorScheme

        var isRememberMode by remember { mutableStateOf(false) }

        return Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            QTextForm(
                labelId = R.string.login_name,
                onValueChange = {
                    viewModel.updateLoginState(state.copy(name = it))
                },
                value = state.name,
                imeAction = ImeAction.Next
            )
            QTextForm(
                labelId = R.string.login_email,
                onValueChange = {
                    viewModel.updateLoginState(state.copy(email = it))
                },
                value = state.email,
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.email),
                        contentDescription = "Email Icon",
                    )
                },
                imeAction = ImeAction.Next
            )
            QTextForm(
                labelId = R.string.login_password,
                onValueChange = {
                    viewModel.updateLoginState(state.copy(password = it))
                },
                value = state.password,
                obscured = true
            )
            if (state.error.isNotEmpty()) Text(
                state.error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                onClick = {
                    focusManager.clearFocus()
                    CoroutineScope(Dispatchers.Main).launch {
                        if (isRememberMode) viewModel.createUser()
                        else viewModel.signInUser()
                    }
                }
            ) {
                Text(stringResource(if (isRememberMode) { R.string.register_button } else { R.string.login_button }))
            }
            Text(
                stringResource(if (isRememberMode) { R.string.register_toggle_2 } else { R.string.register_toggle_1 }),
                modifier = Modifier
                    .clickable {
                        isRememberMode = !isRememberMode
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
