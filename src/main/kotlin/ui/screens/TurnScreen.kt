package ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
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
        onNextClicked = { viewModel.setNextPlayerActive(it) },
        onSetTableOrder = { viewModel.setOrderFromTable() },
        randomAnimType = uiState.randomAnimType,
        onSetRandomAnimType = { viewModel.setRandomAnimType(it) },
        onMovePlayer = { playerId, targetPos ->  viewModel.movePlayerPosition(playerId, targetPos) },
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
    onNextClicked: (forward: Boolean) -> Unit,
    onSetTableOrder: () -> Unit,
    onMovePlayer: (fromPos: Int, targetPos: Int) -> Unit,
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
                    onMovePlayer = onMovePlayer,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Column {
                TurnButtons(
                    onPreviousClicked = { onNextClicked(false) },
                    onNextClicked = { onNextClicked(true) },
                    modifier = Modifier.fillMaxWidth()
                )
                RandomTypeSelect(
                    type = randomAnimType,
                    onSetType = onSetRandomAnimType
                )
            }
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
    onMovePlayer: (fromPos: Int, targetPos: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var editTarget by remember { mutableStateOf<Int?>(null) }

    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        itemsIndexed(
            items = playerList,
            key = { _, item -> item.id }
        ) { i, player ->
            val target = editTarget

            val reorderItemState = when {
                target == null -> ReorderItemState.DEFAULT
                target == i -> ReorderItemState.REORDER_ACTIVE
                target > i -> ReorderItemState.ABOVE_TARGET
                else -> ReorderItemState.BELOW_TARGET
            }

            PlayerItem(
                item = player,
                isActive = player.id == activePlayerId,
                onSetActive = { onSetActivePlayer(player.id) },
                onSetPseudoRandomActive = { onSetPseudoRandomActive(player.id) },
                itemReorderState = reorderItemState,
                onReorderClicked = {
                    editTarget = when (reorderItemState) {
                        ReorderItemState.ABOVE_TARGET,
                        ReorderItemState.BELOW_TARGET -> {
                            target?.let { onMovePlayer(it, i) }
                            null
                        }
                        ReorderItemState.REORDER_ACTIVE -> null
                        ReorderItemState.DEFAULT -> i
                    }
                },
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
    itemReorderState: ReorderItemState,
    onReorderClicked: () -> Unit,
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
    ) {
        IconButton(
            onClick = onReorderClicked
        ) {
            ReorderIcon(itemReorderState)
        }
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
fun ReorderIcon(
    state: ReorderItemState,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = state,
        modifier = modifier
    ) { target ->
        val targetDrawable = when (target) {
            ReorderItemState.ABOVE_TARGET -> Icons.Outlined.ArrowUpward
            ReorderItemState.BELOW_TARGET -> Icons.Outlined.ArrowDownward
            ReorderItemState.REORDER_ACTIVE -> Icons.Outlined.Cancel
            ReorderItemState.DEFAULT -> Icons.Outlined.Reorder
        }

        Icon(
            imageVector = targetDrawable,
            contentDescription = null
        )
    }
}

enum class ReorderItemState {
    ABOVE_TARGET,
    BELOW_TARGET,
    REORDER_ACTIVE,
    DEFAULT
}

@Composable
fun TurnButtons(
    onNextClicked: () -> Unit,
    onPreviousClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
            .padding(8.dp)
    ) {
        TurnButton(
            icon = Icons.Outlined.KeyboardDoubleArrowLeft,
            title = "Previous",
            onClick = onPreviousClicked
        )
        TurnButton(
            icon = Icons.Outlined.KeyboardDoubleArrowRight,
            title = "Next",
            onClick = onNextClicked
        )
    }
}

@Composable
fun TurnButton(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = onClick)
            .size(125.dp)
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            style = MaterialTheme.typography.titleLarge
        )
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
