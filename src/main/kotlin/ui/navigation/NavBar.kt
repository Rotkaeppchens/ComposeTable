package ui.navigation

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import ui.theme.AppTheme

@Composable
fun NavBar(
    navTarget: NavigationTargets,
    onExitClicked: () -> Unit,
    modifier: Modifier = Modifier,
    onNavTargetClicked: (target: NavigationTargets) -> Unit
) {
    NavigationRail (
        modifier = modifier
    ) {
        NavItem(
            navTarget = NavigationTargets.STATUS,
            currentNavTarget = navTarget,
            title = "Status",
            icon = Icons.Outlined.BlurOn,
            onNavTargetClicked = onNavTargetClicked
        )

        Spacer(
            modifier = Modifier
                .weight(1f)
        )

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

        Spacer(
            modifier = Modifier
                .weight(1f)
        )

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

        Spacer(
            modifier = Modifier
                .weight(1f)
        )

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
