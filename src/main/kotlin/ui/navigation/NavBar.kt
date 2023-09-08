package ui.navigation

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ui.theme.AppTheme

@Composable
fun NavBar(
    navTarget: NavigationTargets,
    onExitClicked: () -> Unit,
    modifier: Modifier = Modifier,
    onNavTargetClicked: (target: NavigationTargets) -> Unit
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
        NavItem(
            navTarget = NavigationTargets.STATUS,
            currentNavTarget = navTarget,
            title = "Status",
            icon = Icons.Outlined.BlurOn,
            onNavTargetClicked = onNavTargetClicked
        )

        Spacer(Modifier.height(16.dp))

        NavItem(
            navTarget = NavigationTargets.PLAYERS,
            currentNavTarget = navTarget,
            title = "Players",
            icon = Icons.Outlined.Person,
            onNavTargetClicked = onNavTargetClicked
        )
        NavItem(
            navTarget = NavigationTargets.TABLE_SETUP,
            currentNavTarget = navTarget,
            title = "Table",
            icon = Icons.Outlined.Hexagon,
            onNavTargetClicked = onNavTargetClicked
        )

        Spacer(Modifier.height(16.dp))

        NavItem(
            navTarget = NavigationTargets.TIMER,
            currentNavTarget = navTarget,
            title = "Timer",
            icon = Icons.Outlined.Timer,
            onNavTargetClicked = onNavTargetClicked
        )
        NavItem(
            navTarget = NavigationTargets.HEALTH,
            currentNavTarget = navTarget,
            title = "Health",
            icon = Icons.Outlined.Bloodtype,
            onNavTargetClicked = onNavTargetClicked
        )

        Spacer(Modifier.height(16.dp))

        NavItem(
            navTarget = NavigationTargets.SETTINGS,
            currentNavTarget = navTarget,
            title = "Settings",
            icon = Icons.Outlined.Settings,
            onNavTargetClicked = onNavTargetClicked
        )
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
    navTarget: NavigationTargets,
    currentNavTarget: NavigationTargets,
    title: String,
    icon: ImageVector,
    onNavTargetClicked: (target: NavigationTargets) -> Unit,
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

@Preview
@Composable
fun NavBarPreview() {
    AppTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            NavBar(NavigationTargets.STATUS, onExitClicked = {}) {  }
        }
    }
}
