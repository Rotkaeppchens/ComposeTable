package ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import ui.navigation.builder.NavListBuilder
import ui.screens.*

val NavTargetList = NavListBuilder {
    group("Status") {
        target {
            title = "Status"
            icon = Icons.Outlined.BlurOn
            screen = { StatusScreen(modifier = it) }
            isDefaultTarget = true
        }
    }
    group("Setup") {
        target {
            title = "Players"
            icon = Icons.Outlined.Person
            screen = { PlayerScreen(modifier = it) }
        }
        target {
            title = "Table"
            icon = Icons.Outlined.Hexagon
            screen = { TableSetupScreen(modifier = it) }
        }
    }
    group("Screens") {
        target {
            title = "Timer"
            icon = Icons.Outlined.Timer
            screen = { TimerScreen(modifier = it) }
        }
        target {
            title = "Health"
            icon = Icons.Outlined.Bloodtype
            screen = { HealthScreen(modifier = it) }
        }
        target {
            title = "Turn"
            icon = Icons.Outlined.SelfImprovement
            screen = { TurnScreen(modifier = it) }
        }
        target {
            title = "Effects"
            icon = Icons.Outlined.Flare
            screen = { EffectScreen(modifier = it) }
        }
    }
    group("Settings") {
        target {
            title = "Settings"
            icon = Icons.Outlined.Settings
            screen = { SettingsScreen(modifier = it) }
        }
    }
}
