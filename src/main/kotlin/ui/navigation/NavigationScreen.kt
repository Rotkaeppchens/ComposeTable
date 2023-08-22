package ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ui.screens.*

@Composable
fun NavigationScreen(
    target: NavigationTargets,
    modifier: Modifier = Modifier
) {
    when(target) {
        NavigationTargets.STATUS -> StatusScreen(modifier = modifier)
        NavigationTargets.PLAYERS -> PlayerScreen(modifier = modifier)
        NavigationTargets.TIMER -> TimerScreen(modifier = modifier)
        NavigationTargets.TABLE_SETUP -> TableSetupScreen(modifier = modifier)
        NavigationTargets.SETTINGS -> SettingsScreen(modifier = modifier)
    }
}
