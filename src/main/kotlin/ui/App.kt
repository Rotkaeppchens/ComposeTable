package ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ui.theme.AppTheme

@Composable
fun App(
    useDarkTheme: Boolean,
    onExit: () -> Unit
) {
    AppTheme(
        useDarkTheme = useDarkTheme
    ) {
        Surface(
            modifier = Modifier
                .border(1.dp, MaterialTheme.colorScheme.onSurface)
                .fillMaxSize()
        ) {
        }
    }
}
