package ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.PersonRemove
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import data.entities.Player
import org.koin.compose.koinInject
import ui.composables.ColorSelector
import ui.composables.PlayerList
import ui.toColor
import view_models.PlayerViewModel

@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel = koinInject(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    PlayerScreen(
        playerList = uiState.players,
        onSave = { id, name, color ->
            viewModel.setPlayerName(id, name)
            viewModel.setPlayerColor(id, color)
        },
        addPlayerClicked = { viewModel.addPlayer() },
        removePlayerClicked = { viewModel.removePlayer(it) },
        modifier = modifier
    )
}

@Composable
fun PlayerScreen(
    playerList: List<Player>,
    onSave: (playerId: Int, name: String, color: Color) -> Unit,
    addPlayerClicked: () -> Unit,
    removePlayerClicked: (playerId: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val (activePlayerId, setActivePlayerId) = remember { mutableStateOf<Int?>(null) }
    val activePlayer = remember(activePlayerId) { playerList.find { it.id == activePlayerId } }

    Row(
        modifier = modifier
    ) {
        PlayerListWithIcons(
            playerList = playerList,
            activePlayerId = activePlayerId,
            setActivePlayerId = setActivePlayerId,
            addPlayerClicked = addPlayerClicked,
            removePlayerClicked = {
                if (activePlayerId != null) {
                    removePlayerClicked(activePlayerId)
                    setActivePlayerId(null)
                }
            }
        )
        Spacer(modifier = Modifier.width(8.dp))
        if (activePlayer != null) {
            val (playerNameInput, setPlayerNameInput) = remember(activePlayer) {
                mutableStateOf(activePlayer.name)
            }
            val (selectedColor, setSelectedColor) = remember(activePlayer) {
                mutableStateOf(activePlayer.color.toColor())
            }

            CurrentPlayerInput(
                playerNameInput = playerNameInput,
                setPlayerNameInput = setPlayerNameInput,
                onSaveClicked = {
                    onSave(activePlayer.id, playerNameInput, selectedColor)
                },
                onResetClicked = {
                    setPlayerNameInput(activePlayer.name)
                    setSelectedColor(activePlayer.color.toColor())
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
                    .fillMaxHeight()
            )
            ColorSelector(
                color = selectedColor,
                onColorChange = setSelectedColor,
                modifier = Modifier
                    .width(250.dp)
                    .fillMaxHeight()
            )
        }
    }
}

@Composable
fun PlayerListWithIcons(
    playerList: List<Player>,
    activePlayerId: Int?,
    setActivePlayerId: (Int) -> Unit,
    addPlayerClicked: () -> Unit,
    removePlayerClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.width(200.dp)
    ) {
        PlayerList(
            players = playerList,
            onPlayerClick = setActivePlayerId,
            activePlayerId = activePlayerId,
            modifier = Modifier.weight(1f)
        )
        Divider(
            thickness = Dp.Hairline,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            FilledIconButton(
                onClick = addPlayerClicked
            ) {
                Icon(
                    imageVector = Icons.Outlined.PersonAdd,
                    contentDescription = null
                )
            }
            FilledIconButton(
                enabled = activePlayerId != null,
                onClick = removePlayerClicked
            ) {
                Icon(
                    imageVector = Icons.Outlined.PersonRemove,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun CurrentPlayerInput(
    playerNameInput: String,
    setPlayerNameInput: (name: String) -> Unit,
    onSaveClicked: () -> Unit,
    onResetClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        OutlinedTextField(
            value = playerNameInput,
            onValueChange = setPlayerNameInput,
            label = {
                Text("Player Name")
            },
            maxLines = 1
        )
        Spacer(Modifier.height(8.dp))
        Row {
            Button(
                onClick = onSaveClicked,
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding
            ) {
                Icon(
                    imageVector = Icons.Outlined.Save,
                    contentDescription = null,
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Save")
            }
            Spacer(Modifier.width(16.dp))
            FilledTonalIconButton(
                onClick = onResetClicked
            ) {
                Icon(
                    imageVector = Icons.Outlined.RestartAlt,
                    contentDescription = null
                )
            }
        }
    }
}
