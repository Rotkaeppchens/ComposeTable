package ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import ui.navigation.builder.NavTarget

@Composable
fun NavScreen(
    navState: NavTarget,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = navState,
        transitionSpec = {
            val slideDir = if (targetState.id > initialState.id) {
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
        targetState.screen(modifier)
    }
}
