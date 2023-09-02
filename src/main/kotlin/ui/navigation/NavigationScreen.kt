package ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import ui.screens.*

@Composable
fun NavigationScreen(
    navState: NavigationTargets,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = navState,
        transitionSpec = {
            val slideDir = if (targetState.ordinal > initialState.ordinal) {
                AnimatedContentTransitionScope.SlideDirection.Up
            } else {
                AnimatedContentTransitionScope.SlideDirection.Down
            }

            val animSpec = tween<IntOffset>(
                durationMillis = 100
            )

            slideIntoContainer(
                towards = slideDir,
                animationSpec = animSpec
            ) togetherWith slideOutOfContainer(
                towards = slideDir,
                animationSpec = animSpec
            )
        },
        modifier = Modifier.fillMaxSize()
    ) {targetState ->
        when(targetState) {
            NavigationTargets.STATUS -> StatusScreen(modifier = modifier)
            NavigationTargets.PLAYERS -> PlayerScreen(modifier = modifier)
            NavigationTargets.TABLE_SETUP -> TableSetupScreen(modifier = modifier)
            NavigationTargets.TIMER -> TimerScreen(modifier = modifier)
            NavigationTargets.HEALTH -> HealthScreen(modifier = modifier)
            NavigationTargets.SETTINGS -> SettingsScreen(modifier = modifier)
        }
    }
}
