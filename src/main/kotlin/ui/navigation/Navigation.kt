package ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ui.screens.*

enum class NavigationTargets {
    STATUS,
    SETTINGS,
}

@Composable
fun NavigationScreen(
    target: NavigationTargets,
    modifier: Modifier = Modifier
) {
    when(target) {
        NavigationTargets.STATUS -> StatusScreen(modifier = modifier)
        NavigationTargets.SETTINGS -> SettingsScreen(modifier = modifier)
    }
}
