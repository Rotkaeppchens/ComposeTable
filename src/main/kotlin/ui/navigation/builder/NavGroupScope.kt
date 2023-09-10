package ui.navigation.builder

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

class NavGroupScope(
    private val onAddTarget: (
        title: String,
        icon: ImageVector,
        isDefaultTarget: Boolean,
        screen: @Composable (Modifier) -> Unit,
    ) -> Unit
) {
    fun target(builder: NavTargetScope.() -> Unit) {
        val scope = NavTargetScope()
        builder(scope)

        onAddTarget(
            scope.title,
            scope.icon,
            scope.isDefaultTarget,
            scope.screen
        )
    }
}
