package ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Hexagon
import androidx.compose.material.icons.outlined.KeyboardDoubleArrowRight
import androidx.compose.material.icons.outlined.Shuffle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import data.entities.Player
import org.koin.compose.koinInject
import view_models.TurnViewModel

@Composable
fun TurnScreen(
    viewModel: TurnViewModel = koinInject(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    TurnScreen(
        activePlayerId = uiState.activePlayerId,
        playerList = uiState.playerList,
        onSetActivePlayer = { viewModel.setPlayerActive(it) },
        onSetRandomPlayerActive = { viewModel.setRandomPlayerActive() },
        onSetPseudoRandomActive = { viewModel.setPseudoRandomActive(it) },
        onNextClicked = { viewModel.setNextPlayerActive() },
        onSetTableOrder = { viewModel.setOrderFromTable() },
        modifier = modifier
    )
}

@Composable
fun TurnScreen(
    activePlayerId: Int,
    playerList: List<Player>,
    onSetActivePlayer: (Int) -> Unit,
    onSetRandomPlayerActive: () -> Unit,
    onSetPseudoRandomActive: (Int) -> Unit,
    onNextClicked: () -> Unit,
    onSetTableOrder: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End
            ) {
                ExtendedFloatingActionButton(
                    text = {
                        Text("Next")
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.KeyboardDoubleArrowRight,
                            contentDescription = null
                        )
                    },
                    onClick = onNextClicked
                )
                Spacer(Modifier.height(8.dp))
                ExtendedFloatingActionButton(
                    text = {
                        Text("Random")
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Shuffle,
                            contentDescription = null
                        )
                    },
                    onClick = onSetRandomPlayerActive
                )
            }
        },
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(300.dp)
                .padding(it)
        ) {
            Button(
                onClick = onSetTableOrder,
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Hexagon,
                    contentDescription = null,
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Table Order")
            }
            Divider()
            TurnList(
                activePlayerId = activePlayerId,
                playerList = playerList,
                onSetActivePlayer = onSetActivePlayer,
                onSetPseudoRandomActive = onSetPseudoRandomActive,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TurnList(
    activePlayerId: Int,
    playerList: List<Player>,
    onSetActivePlayer: (Int) -> Unit,
    onSetPseudoRandomActive: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        itemsIndexed(
            items = playerList,
            key = { _, item -> item.id }
        ) { i, player ->
            PlayerItem(
                index = i,
                item = player,
                isActive = player.id == activePlayerId,
                onSetActive = { onSetActivePlayer(player.id) },
                onSetPseudoRandomActive = { onSetPseudoRandomActive(player.id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItemPlacement()
            )
        }
    }
}

@Composable
fun PlayerItem(
    index: Int,
    item: Player,
    isActive: Boolean,
    onSetActive: () -> Unit,
    onSetPseudoRandomActive: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        if (isActive) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.secondaryContainer
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(
                backgroundColor,
                shape = MaterialTheme.shapes.medium
            )
            .padding(start = 8.dp)
    ) {
        Text(
            text = index.toString(),
            fontStyle = FontStyle.Italic,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = item.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        IconButton(
            onClick = onSetActive
        ) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null
            )
        }
        IconButton(
            onClick = onSetPseudoRandomActive
        ) {
            Icon(
                imageVector = Icons.Outlined.Shuffle,
                contentDescription = null
            )
        }
    }
}
