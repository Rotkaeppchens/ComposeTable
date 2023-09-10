package ui.navigation.builder

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

class NavBuilderScope(
    private val onAddTarget: (
        title: String,
        icon: ImageVector,
        groupName: String,
        isDefaultTarget: Boolean,
        screen: @Composable (Modifier) -> Unit,
    ) -> Unit
) {
    fun group(groupName: String, builder: NavGroupScope.() -> Unit) {
        builder(
            NavGroupScope { title, icon, isDefaultTarget, screen ->
                onAddTarget(title, icon, groupName, isDefaultTarget, screen)
            }
        )
    }
}
