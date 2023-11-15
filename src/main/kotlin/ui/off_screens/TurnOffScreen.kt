package ui.off_screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import data.repositories.TurnRepository
import org.koin.compose.koinInject

@Composable
fun TurnOffScreen(
    turnRepo: TurnRepository = koinInject(),
    modifier: Modifier = Modifier
) {
    val turnList by turnRepo.turnList.collectAsState()

    LazyColumn(
        modifier = modifier
    ) {
        items(turnList) { slot ->
            Row {
                Checkbox(
                    checked = slot.active,
                    onCheckedChange = null
                )
                Text("${slot.slotId}: ${slot.name}")
                if (slot.onDeck) {
                    Text("ON DECK")
                }
            }
        }
    }
}
