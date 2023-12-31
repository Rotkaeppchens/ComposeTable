package ui.navigation

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
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
            .width(IntrinsicSize.Min)
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
            NavDivider()
        }

        NavigationRailItem(
            selected = false,
            onClick = onExitClicked,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = null
                )
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

@Composable
fun NavDivider(
    modifier: Modifier = Modifier
) {
    Divider(
        modifier = modifier
            .padding(vertical = 4.dp, horizontal = 8.dp)
    )
}
