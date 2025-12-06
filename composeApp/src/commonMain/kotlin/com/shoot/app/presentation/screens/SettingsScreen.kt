package com.shoot.app.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.shoot.app.design.components.ShootAlertDialog
import com.shoot.app.design.components.ShootButton
import com.shoot.app.design.components.ShootErrorDialog
import com.shoot.app.design.components.ShootTextField
import com.shoot.app.domain.model.User
import com.shoot.app.domain.model.UserStatus
import com.shoot.app.presentation.auth.LoginScreen
import com.shoot.app.presentation.common.UiState
import com.shoot.app.presentation.settings.SettingsViewModel

class SettingsScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel: SettingsViewModel = koinScreenModel()
        SettingsScreenContent(viewModel = viewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreenContent(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val navigator = LocalNavigator.currentOrThrow

    val userState by viewModel.userState.collectAsState()
    val logoutState by viewModel.logoutState.collectAsState()
    val updateProfileState by viewModel.updateProfileState.collectAsState()
    val changePasswordState by viewModel.changePasswordState.collectAsState()
    val deleteAccountState by viewModel.deleteAccountState.collectAsState()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    val darkModeEnabled by viewModel.darkModeEnabled.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadCurrentUser()
    }

    // Handle logout success
    LaunchedEffect(logoutState) {
        if (logoutState is UiState.Success) {
            navigator.replaceAll(LoginScreen())
        }
    }

    // Handle delete account success
    LaunchedEffect(deleteAccountState) {
        if (deleteAccountState is UiState.Success) {
            navigator.replaceAll(LoginScreen())
        } else if (deleteAccountState is UiState.Error) {
            errorMessage = (deleteAccountState as UiState.Error).message
            viewModel.resetDeleteAccountState()
        }
    }

    // Handle update profile result
    LaunchedEffect(updateProfileState) {
        if (updateProfileState is UiState.Success) {
            showEditProfileDialog = false
            viewModel.resetUpdateProfileState()
        } else if (updateProfileState is UiState.Error) {
            errorMessage = (updateProfileState as UiState.Error).message
            viewModel.resetUpdateProfileState()
        }
    }

    // Handle change password result
    LaunchedEffect(changePasswordState) {
        if (changePasswordState is UiState.Success) {
            showChangePasswordDialog = false
            viewModel.resetChangePasswordState()
        } else if (changePasswordState is UiState.Error) {
            errorMessage = (changePasswordState as UiState.Error).message
            viewModel.resetChangePasswordState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("설정") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Profile Section
            item {
                when (userState) {
                    is UiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    is UiState.Success -> {
                        ProfileSection(
                            user = (userState as UiState.Success<User>).data,
                            onEditClick = { showEditProfileDialog = true }
                        )
                    }
                    is UiState.Error -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (userState as UiState.Error).message,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    else -> {}
                }
            }

            item { SectionDivider() }

            // Account Settings
            item {
                SectionHeader(title = "계정")
            }

            item {
                SettingsItem(
                    icon = Icons.Default.Lock,
                    title = "비밀번호 변경",
                    onClick = { showChangePasswordDialog = true }
                )
            }

            item { SectionDivider() }

            // App Settings
            item {
                SectionHeader(title = "앱 설정")
            }

            item {
                SettingsItemWithSwitch(
                    icon = Icons.Default.Notifications,
                    title = "알림",
                    subtitle = "푸시 알림 받기",
                    checked = notificationsEnabled,
                    onCheckedChange = { viewModel.toggleNotifications(it) }
                )
            }

            item { SectionDivider() }

            // Information
            item {
                SectionHeader(title = "정보")
            }

            item {
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "앱 정보",
                    subtitle = "버전 1.0.0",
                    onClick = { showAboutDialog = true }
                )
            }

            item { SectionDivider() }

            // Danger Zone
            item {
                SectionHeader(title = "계정 관리")
            }

            item {
                SettingsItem(
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    title = "로그아웃",
                    onClick = { showLogoutDialog = true },
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            item {
                SettingsItem(
                    icon = Icons.Default.Delete,
                    title = "회원 탈퇴",
                    subtitle = "계정이 영구적으로 삭제됩니다",
                    onClick = { showDeleteAccountDialog = true },
                    tint = MaterialTheme.colorScheme.error
                )
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }

    // Dialogs
    if (showEditProfileDialog && userState is UiState.Success) {
        EditProfileDialog(
            user = (userState as UiState.Success<User>).data,
            isLoading = updateProfileState is UiState.Loading,
            onDismiss = { showEditProfileDialog = false },
            onSave = { nickname, bio ->
                viewModel.updateProfile(nickname = nickname, bio = bio)
            }
        )
    }

    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            isLoading = changePasswordState is UiState.Loading,
            onDismiss = { showChangePasswordDialog = false },
            onSave = { current, new ->
                viewModel.changePassword(current, new)
            }
        )
    }

    if (showLogoutDialog) {
        ShootAlertDialog(
            title = "로그아웃",
            message = "정말 로그아웃 하시겠습니까?",
            confirmText = "로그아웃",
            dismissText = "취소",
            onConfirm = {
                showLogoutDialog = false
                viewModel.logout()
            },
            onDismiss = { showLogoutDialog = false }
        )
    }

    if (showDeleteAccountDialog) {
        ShootAlertDialog(
            title = "회원 탈퇴",
            message = "정말 탈퇴하시겠습니까?\n모든 데이터가 영구적으로 삭제되며 복구할 수 없습니다.",
            confirmText = "탈퇴",
            dismissText = "취소",
            onConfirm = {
                showDeleteAccountDialog = false
                viewModel.deleteAccount()
            },
            onDismiss = { showDeleteAccountDialog = false }
        )
    }

    if (showAboutDialog) {
        AboutDialog(
            onDismiss = { showAboutDialog = false }
        )
    }

    errorMessage?.let { message ->
        ShootErrorDialog(
            message = message,
            onDismiss = { errorMessage = null }
        )
    }
}

@Composable
private fun ProfileSection(
    user: User,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onEditClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile Image
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            if (user.profileImageUrl != null) {
                // TODO: Load image with Coil
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.nickname,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "@${user.username}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (!user.bio.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = user.bio,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            user.userCode?.let { code ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "친구 코드: $code",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }

        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "프로필 수정",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun SectionDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier.padding(vertical = 8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    )
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
    tint: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = tint
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SettingsItemWithSwitch(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun EditProfileDialog(
    user: User,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onSave: (nickname: String, bio: String?) -> Unit
) {
    var nickname by remember { mutableStateOf(user.nickname) }
    var bio by remember { mutableStateOf(user.bio ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("프로필 수정") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ShootTextField(
                    value = nickname,
                    onValueChange = { nickname = it },
                    label = "닉네임",
                    modifier = Modifier.fillMaxWidth()
                )

                ShootTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = "상태 메시지",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            ShootButton(
                text = "저장",
                onClick = { onSave(nickname, bio.ifBlank { null }) },
                loading = isLoading,
                enabled = nickname.isNotBlank()
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}

@Composable
private fun ChangePasswordDialog(
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onSave: (currentPassword: String, newPassword: String) -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("비밀번호 변경") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ShootTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = "현재 비밀번호",
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                ShootTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = "새 비밀번호",
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                ShootTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = "새 비밀번호 확인",
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                error?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            ShootButton(
                text = "변경",
                onClick = {
                    when {
                        currentPassword.isBlank() -> error = "현재 비밀번호를 입력하세요"
                        newPassword.length < 6 -> error = "새 비밀번호는 6자 이상이어야 합니다"
                        newPassword != confirmPassword -> error = "새 비밀번호가 일치하지 않습니다"
                        else -> {
                            error = null
                            onSave(currentPassword, newPassword)
                        }
                    }
                },
                loading = isLoading
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}

@Composable
private fun AboutDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Shoot App") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("버전: 1.0.0")
                Text("Kotlin Multiplatform으로 개발된 크로스 플랫폼 메시징 앱입니다.")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tech Stack:",
                    style = MaterialTheme.typography.titleSmall
                )
                Text("- Kotlin 2.2.20")
                Text("- Compose Multiplatform 1.7.1")
                Text("- Ktor 3.0.3")
                Text("- SqlDelight 2.0.2")
                Text("- Koin 4.0.1")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("확인")
            }
        }
    )
}
