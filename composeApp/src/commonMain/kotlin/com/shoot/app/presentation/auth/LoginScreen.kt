package com.shoot.app.presentation.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.shoot.app.design.components.ButtonSize
import com.shoot.app.design.components.ShootButton
import com.shoot.app.design.components.ShootTextField
import com.shoot.app.design.components.ShootTextButton
import com.shoot.app.design.theme.DefaultSpacing
import com.shoot.app.presentation.common.UiState
import com.shoot.app.presentation.common.isLoading
import com.shoot.app.presentation.navigation.MainScreen

class LoginScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<AuthViewModel>()
        val navigator = LocalNavigator.currentOrThrow
        val loginState by viewModel.loginState.collectAsState()

        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var showErrorDialog by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf("") }

        LaunchedEffect(loginState) {
            when (loginState) {
                is UiState.Success -> {
                    // Navigate to main screen on successful login
                    navigator.replace(MainScreen())
                    viewModel.resetLoginState()
                }
                is UiState.Error -> {
                    errorMessage = (loginState as UiState.Error).message
                    showErrorDialog = true
                }
                else -> {}
            }
        }

        if (showErrorDialog) {
            ErrorDialog(
                message = errorMessage,
                onDismiss = {
                    showErrorDialog = false
                    viewModel.resetLoginState()
                }
            )
        }

        LoginScreenContent(
            username = username,
            password = password,
            isLoading = loginState.isLoading(),
            onUsernameChange = { username = it },
            onPasswordChange = { password = it },
            onLoginClick = {
                if (username.isNotBlank() && password.isNotBlank()) {
                    viewModel.login(username, password)
                } else {
                    errorMessage = "Please enter username and password"
                    showErrorDialog = true
                }
            },
            onRegisterClick = {
                navigator.push(RegisterScreen())
            }
        )
    }
}

@Composable
fun LoginScreenContent(
    username: String,
    password: String,
    isLoading: Boolean,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(DefaultSpacing.large),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(DefaultSpacing.medium)
            ) {
                Text(
                    text = "Shoot App",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(DefaultSpacing.large))

                ShootTextField(
                    value = username,
                    onValueChange = onUsernameChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = "Username",
                    placeholder = "Enter your username",
                    leadingIcon = Icons.Filled.Person,
                    enabled = !isLoading,
                    singleLine = true,
                    imeAction = ImeAction.Next
                )

                ShootTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = "Password",
                    placeholder = "Enter your password",
                    leadingIcon = Icons.Filled.Lock,
                    enabled = !isLoading,
                    singleLine = true,
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                    onImeAction = onLoginClick,
                    visualTransformation = PasswordVisualTransformation()
                )

                Spacer(modifier = Modifier.height(DefaultSpacing.small))

                ShootButton(
                    text = "Login",
                    onClick = onLoginClick,
                    modifier = Modifier.fillMaxWidth(),
                    loading = isLoading,
                    size = ButtonSize.Large
                )

                ShootTextButton(
                    text = "Don't have an account? Sign up",
                    onClick = onRegisterClick,
                    enabled = !isLoading
                )
            }
        }
    }
}

@Composable
fun ErrorDialog(
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Error") },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}
