package ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import data.modules.EffectsModule
import org.koin.compose.koinInject
import view_models.EffectsViewModel

@Composable
fun EffectScreen(
    viewModel: EffectsViewModel = koinInject(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Row(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            Button(
                onClick = { viewModel.startBattle() }
            ) {
                Text("Start Battle")
            }
            Button(
                onClick = { viewModel.endBattle() }
            ) {
                Text("End Battle")
            }
        }
        ActiveEffectsList(
            uiState.activeEffects,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ActiveEffectsList(
    activeEffects: Set<EffectsModule.OneShotEffects>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        item {
            Text("Active Effects:")
        }
        items(activeEffects.toList()) {
            Text(it.name)
        }
    }
}
