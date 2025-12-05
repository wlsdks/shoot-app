package com.shoot.app.presentation.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.shoot.app.design.components.ButtonSize
import com.shoot.app.design.components.ShootButton
import com.shoot.app.design.components.ShootTextField
import com.shoot.app.design.components.ShootTextButton
import com.shoot.app.design.theme.DefaultSpacing
import com.shoot.app.presentation.common.UiState
import com.shoot.app.presentation.common.isLoading

class RegisterScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<AuthViewModel>()
        val navigator = LocalNavigator.currentOrThrow
        val registerState by viewModel.registerState.collectAsState()

        var username by remember { mutableStateOf("") }
        var nickname by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var passwordConfirm by remember { mutableStateOf("") }
        var showErrorDialog by remember { mutableStateOf(false) }
        var showSuccessDialog by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf("") }

        LaunchedEffect(registerState) {
            when (registerState) {
                is UiState.Success -> {
                    showSuccessDialog = true
                }
                is UiState.Error -> {
                    errorMessage = (registerState as UiState.Error).message
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
                    viewModel.resetRegisterState()
                }
            )
        }

        if (showSuccessDialog) {
            SuccessDialog(
                message = "Registration successful! Please login.",
                onConfirm = {
                    showSuccessDialog = false
                    viewModel.resetRegisterState()
                    navigator.pop()
                }
            )
        }

        RegisterScreenContent(
            username = username,
            nickname = nickname,
            email = email,
            password = password,
            passwordConfirm = passwordConfirm,
            isLoading = registerState.isLoading(),
            onUsernameChange = { username = it },
            onNicknameChange = { nickname = it },
            onEmailChange = { email = it },
            onPasswordChange = { password = it },
            onPasswordConfirmChange = { passwordConfirm = it },
            onRegisterClick = {
                val validationError = validateRegistrationInput(
                    username, nickname, email, password, passwordConfirm
                )
                if (validationError != null) {
                    errorMessage = validationError
                    showErrorDialog = true
                } else {
                    viewModel.register(username, nickname, password, email)
                }
            },
            onBackClick = {
                navigator.pop()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreenContent(
    username: String,
    nickname: String,
    email: String,
    password: String,
    passwordConfirm: String,
    isLoading: Boolean,
    onUsernameChange: (String) -> Unit,
    onNicknameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordConfirmChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Account") },
                navigationIcon = {
                    IconButton(onClick = onBackClick, enabled = !isLoading) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(DefaultSpacing.large)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(DefaultSpacing.medium)
            ) {
                Text(
                    text = "Join Shoot App",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(DefaultSpacing.small))

                ShootTextField(
                    value = username,
                    onValueChange = onUsernameChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = "Username",
                    placeholder = "Choose a unique username",
                    leadingIcon = Icons.Filled.Person,
                    enabled = !isLoading,
                    singleLine = true,
                    imeAction = ImeAction.Next
                )

                ShootTextField(
                    value = nickname,
                    onValueChange = onNicknameChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = "Nickname",
                    placeholder = "Your display name",
                    leadingIcon = Icons.Filled.Person,
                    enabled = !isLoading,
                    singleLine = true,
                    imeAction = ImeAction.Next
                )

                ShootTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = "Email",
                    placeholder = "your.email@example.com",
                    leadingIcon = Icons.Filled.Email,
                    enabled = !isLoading,
                    singleLine = true,
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )

                ShootTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = "Password",
                    placeholder = "At least 6 characters",
                    leadingIcon = Icons.Filled.Lock,
                    enabled = !isLoading,
                    singleLine = true,
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next,
                    visualTransformation = PasswordVisualTransformation()
                )

                ShootTextField(
                    value = passwordConfirm,
                    onValueChange = onPasswordConfirmChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = "Confirm Password",
                    placeholder = "Re-enter your password",
                    leadingIcon = Icons.Filled.Lock,
                    enabled = !isLoading,
                    singleLine = true,
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                    onImeAction = onRegisterClick,
                    visualTransformation = PasswordVisualTransformation(),
                    errorMessage = if (passwordConfirm.isNotEmpty() && password != passwordConfirm) {
                        "Passwords do not match"
                    } else null
                )

                Spacer(modifier = Modifier.height(DefaultSpacing.small))

                ShootButton(
                    text = "Sign Up",
                    onClick = onRegisterClick,
                    modifier = Modifier.fillMaxWidth(),
                    loading = isLoading,
                    size = ButtonSize.Large
                )

                ShootTextButton(
                    text = "Already have an account? Login",
                    onClick = onBackClick,
                    enabled = !isLoading
                )
            }
        }
    }
}

@Composable
fun SuccessDialog(
    message: String,
    onConfirm: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onConfirm,
        title = { Text("Success") },
        text = { Text(message) },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = onConfirm) {
                Text("OK")
            }
        }
    )
}

fun validateRegistrationInput(
    username: String,
    nickname: String,
    email: String,
    password: String,
    passwordConfirm: String
): String? {
    return when {
        username.isBlank() -> "Username is required"
        username.length < 3 -> "Username must be at least 3 characters"
        nickname.isBlank() -> "Nickname is required"
        email.isBlank() -> "Email is required"
        !email.contains("@") -> "Invalid email format"
        password.isBlank() -> "Password is required"
        password.length < 6 -> "Password must be at least 6 characters"
        passwordConfirm.isBlank() -> "Please confirm your password"
        password != passwordConfirm -> "Passwords do not match"
        else -> null
    }
}
