package ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
            }
        }
    }
}
