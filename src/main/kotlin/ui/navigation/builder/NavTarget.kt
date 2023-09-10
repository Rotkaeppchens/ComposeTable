package ui.navigation.builder

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

data class NavTarget(
    val id: Int,
    val title: String,
    val icon: ImageVector,
    val groupName: String,
    val screen: @Composable (Modifier) -> Unit,
)
