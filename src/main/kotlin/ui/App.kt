package ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import ui.navigation.NavBar
import ui.navigation.NavigationScreen
import ui.navigation.NavigationTargets
import ui.theme.AppTheme

@Composable
fun App(
    useDarkTheme: Boolean,
    onExit: () -> Unit
) {
    val (navTarget, setNavTarget) = remember { mutableStateOf(NavigationTargets.STATUS) }

    AppViewConfiguration {
        AppTheme(
            useDarkTheme = useDarkTheme
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Row {
                    NavBar(
                        navTarget = navTarget,
                        onNavTargetClicked = setNavTarget,
                        onExitClicked = onExit
                    )
                    NavigationScreen(navTarget)
                }
            }
        }
    }
}
