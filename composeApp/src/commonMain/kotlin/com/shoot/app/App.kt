package com.shoot.app

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.shoot.app.design.theme.ShootTheme
import com.shoot.app.presentation.auth.LoginScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    ShootTheme {
        Navigator(LoginScreen())
    }
}
