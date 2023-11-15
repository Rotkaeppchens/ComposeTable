package ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ui.off_screens.HealthOffScreen
import ui.off_screens.TurnOffScreen
import ui.theme.AppTheme

@Composable
fun AppOffScreen(
    modifier: Modifier = Modifier
) {
    AppTheme {
        Surface(
            modifier = modifier.fillMaxSize()
        ) {
            Row {
                TurnOffScreen(
                    modifier = Modifier
                        .padding(24.dp)
                        .weight(1f)
                        .fillMaxHeight()
                )
                HealthOffScreen(
                    modifier = Modifier
                        .padding(24.dp)
                        .weight(1f)
                )
            }
        }
    }
}
