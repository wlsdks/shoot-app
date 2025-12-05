package com.shoot.app.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.shoot.app.presentation.screens.ChatListScreen
import com.shoot.app.presentation.screens.FriendsScreen
import com.shoot.app.presentation.screens.HomeScreen
import com.shoot.app.presentation.screens.SettingsScreen

class MainScreen : Screen {
    @Composable
    override fun Content() {
        var selectedItem by remember { mutableStateOf<BottomNavItem>(BottomNavItem.Home) }

        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp
                ) {
                    BottomNavItem.items.forEach { item ->
                        NavigationBarItem(
                            selected = selectedItem == item,
                            onClick = { selectedItem = item },
                            icon = {
                                Icon(
                                    imageVector = if (selectedItem == item) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title
                                )
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (selectedItem) {
                    BottomNavItem.Home -> HomeScreen().Content()
                    BottomNavItem.Chat -> ChatListScreen().Content()
                    BottomNavItem.Friends -> FriendsScreen().Content()
                    BottomNavItem.Settings -> SettingsScreen().Content()
                }
            }
        }
    }
}
