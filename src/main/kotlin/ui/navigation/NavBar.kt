package ui.navigation

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ui.navigation.builder.NavTarget

@Composable
fun NavBar(
    navTarget: NavTarget,
    onExitClicked: () -> Unit,
    modifier: Modifier = Modifier,
    onNavTargetClicked: (target: NavTarget) -> Unit
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    NavigationRail (
        modifier = modifier
            .verticalScroll(
                state = scrollState,
            )
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { delta ->
                    scope.launch {
                        scrollState.scrollBy(-delta)
                    }
                },
            )
    ) {
        NavTargetList.targetList.forEach { (_, targets) ->
            targets.forEach { target ->
                NavItem(
                    navTarget = target,
                    currentNavTarget = navTarget,
                    title = target.title,
                    icon = target.icon,
                    onNavTargetClicked = onNavTargetClicked
                )
            }
            Spacer(Modifier.height(16.dp))
        }

        NavigationRailItem(
            selected = false,
            onClick = onExitClicked,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = null
                )
            },
            label = {
                Text(text = "Exit")
            }
        )
    }
}

@Composable
fun NavItem(
    navTarget: NavTarget,
    currentNavTarget: NavTarget,
    title: String,
    icon: ImageVector,
    onNavTargetClicked: (target: NavTarget) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationRailItem(
        selected = navTarget == currentNavTarget,
        onClick = { onNavTargetClicked(navTarget) },
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = title
            )
        },
        label = {
            Text(text = title)
        },
        modifier = modifier
    )
}
