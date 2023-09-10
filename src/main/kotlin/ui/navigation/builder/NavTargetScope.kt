package ui.navigation.builder

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

class NavTargetScope {
    var title: String = ""
    var icon: ImageVector = Icons.Outlined.BrokenImage
    var screen: @Composable (Modifier) -> Unit = {}
    var isDefaultTarget: Boolean = false
}
