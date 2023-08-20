package ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ui.screens.*

enum class NavigationTargets {
    STATUS,
}

@Composable
fun NavigationScreen(
    target: NavigationTargets,
    modifier: Modifier = Modifier
) {
    when(target) {
        NavigationTargets.STATUS -> StatusScreen(modifier = modifier)
    }
}
