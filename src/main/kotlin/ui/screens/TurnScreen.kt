package ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Hexagon
import androidx.compose.material.icons.outlined.KeyboardDoubleArrowRight
import androidx.compose.material.icons.outlined.Shuffle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import data.entities.Player
import data.modules.TurnModule
import org.koin.compose.koinInject
import ui.toColor
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
        randomAnimType = uiState.randomAnimType,
        onSetRandomAnimType = { viewModel.setRandomAnimType(it) },
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
    randomAnimType: TurnModule.RandomAnimationType,
    onSetRandomAnimType: (TurnModule.RandomAnimationType) -> Unit,
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
        Row {
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
            RandomTypeSelect(
                type = randomAnimType,
                onSetType = onSetRandomAnimType
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
        items(
            items = playerList,
            key = { item -> item.id }
        ) { player ->
            PlayerItem(
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
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onSetActive)
            .background(backgroundColor)
            .padding(start = 8.dp)
    ) {
        Surface(
            color = item.color.toColor(),
            shape = CircleShape,
            modifier = Modifier
                .size(15.dp)
        ) {}
        Spacer(Modifier.width(8.dp))
        Text(
            text = item.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
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

@Composable
fun RandomTypeSelect(
    type: TurnModule.RandomAnimationType,
    onSetType: (TurnModule.RandomAnimationType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text("Anim Type:")
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = type == TurnModule.RandomAnimationType.FLASH_SIDES,
                onClick = { onSetType(TurnModule.RandomAnimationType.FLASH_SIDES) }
            )
            Text("FLASH_SIDES")
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = type == TurnModule.RandomAnimationType.DOUBLE_INDICATOR,
                onClick = { onSetType(TurnModule.RandomAnimationType.DOUBLE_INDICATOR) }
            )
            Text("DOUBLE_INDICATOR")
        }
    }
}
