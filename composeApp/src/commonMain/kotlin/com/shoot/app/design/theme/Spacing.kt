package com.shoot.app.design.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Spacing(
    val none: Dp = 0.dp,
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val extraLarge: Dp = 32.dp,
    val huge: Dp = 48.dp,
    val massive: Dp = 64.dp
)

val DefaultSpacing = Spacing()

// Extension properties for common spacing patterns
val Spacing.screenPadding: Dp get() = medium // 16dp for screen edges
val Spacing.cardPadding: Dp get() = medium // 16dp for card content
val Spacing.listItemPadding: Dp get() = medium // 16dp for list items
val Spacing.buttonPadding: Dp get() = small // 8dp for button content
val Spacing.iconPadding: Dp get() = small // 8dp around icons
