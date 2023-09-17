package ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectableGroup
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
        onReversePlayerOrder = { viewModel.reversePlayerOrder() },
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
    onReversePlayerOrder: () -> Unit,
    onMovePlayer: (fromPos: Int, targetPos: Int) -> Unit,
    randomAnimType: TurnModule.RandomAnimationType,
    onSetRandomAnimType: (TurnModule.RandomAnimationType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .width(300.dp)
        ) {
            OrderControls(
                onSetTableOrder = onSetTableOrder,
                onReversePlayerOrder = onReversePlayerOrder,
                modifier = Modifier.fillMaxWidth()
            )
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
        Column(
            modifier = Modifier.fillMaxHeight()
        ) {
            TurnButtons(
                onPreviousClicked = { onNextClicked(false) },
                onNextClicked = { onNextClicked(true) },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
            Divider(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth()
            )
            RandomSelector(
                animType = randomAnimType,
                onSetAnimType = onSetRandomAnimType,
                onStartRandomSelector = onSetRandomPlayerActive,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun OrderControls(
    onSetTableOrder: () -> Unit,
    onReversePlayerOrder: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
            .padding(8.dp)
    ) {
        Button(
            onClick = onReversePlayerOrder,
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
        ) {
            Icon(
                imageVector = Icons.Outlined.SwapVert,
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text("Reverse")
        }
        Button(
            onClick = onSetTableOrder,
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
        ) {
            Icon(
                imageVector = Icons.Outlined.Hexagon,
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text("Table Order")
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
        verticalAlignment = Alignment.CenterVertically,
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
fun RandomSelector(
    animType: TurnModule.RandomAnimationType,
    onSetAnimType: (TurnModule.RandomAnimationType) -> Unit,
    onStartRandomSelector: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        RandomTypeSelect(
            type = animType,
            onSetType = onSetAnimType
        )
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = onStartRandomSelector,
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding
        ) {
            Icon(
                imageVector = Icons.Outlined.Shuffle,
                contentDescription = "Random",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text("Random")
        }
    }
}

@Composable
fun RandomTypeSelect(
    type: TurnModule.RandomAnimationType,
    onSetType: (TurnModule.RandomAnimationType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.selectableGroup()
    ) {
        TextButton(
            onClick = { onSetType(TurnModule.RandomAnimationType.DOUBLE_INDICATOR) },
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding
        ) {
            RadioButton(
                selected = type == TurnModule.RandomAnimationType.DOUBLE_INDICATOR,
                onClick = null,
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text("Rat Race")
        }
        TextButton(
            onClick = { onSetType(TurnModule.RandomAnimationType.FLASH_SIDES) },
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding
        ) {
            RadioButton(
                selected = type == TurnModule.RandomAnimationType.FLASH_SIDES,
                onClick = null,
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text("Flash Sides")
        }
    }
}
