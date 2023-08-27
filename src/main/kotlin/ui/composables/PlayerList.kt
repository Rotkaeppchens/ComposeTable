package ui.composables

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import data.LedColor
import data.entities.Player
import kotlinx.coroutines.launch
import ui.theme.AppTheme
import ui.toColor

@Composable
fun PlayerList(
    players: List<Player>,
    onPlayerClick: (playerId: Int) -> Unit,
    activePlayerId: Int? = null,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        state = lazyListState,
        modifier = modifier
            .width(200.dp)
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { delta ->
                    scope.launch {
                        lazyListState.scrollBy(-delta)
                    }
                },
            )
    ) {
        items(players, key = { it.id }) {
            PlayerItem(
                selected = activePlayerId == it.id,
                playerName = it.name,
                playerColor = it.color.toColor(),
                onClick = { onPlayerClick(it.id) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun PlayerItem(
    selected: Boolean,
    playerName: String,
    playerColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val content: @Composable (RowScope.() -> Unit) = {
        Surface(
            color = playerColor,
            shape = CircleShape,
            modifier = Modifier
                .border(1.dp, MaterialTheme.colorScheme.onPrimary, shape = CircleShape)
                .size(ButtonDefaults.IconSize)
        ) {}
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text(
            text = playerName,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier.fillMaxWidth()
        )
    }

    if (selected) {
        Button(
            onClick = onClick,
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
            modifier = modifier,
            content = content
        )
    } else {
        OutlinedButton(
            onClick = onClick,
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
            modifier = modifier,
            content = content
        )
    }
}

@Preview
@Composable
fun PlayerListPreview() {
    AppTheme {
        PlayerList(
            listOf(
                Player(
                    1, "Test", LedColor()
                ),
                Player(
                    2, "HelloWorld", LedColor(1.0, 0.0, 0.0, 1.0)
                )
            ),
            onPlayerClick = {},
            activePlayerId = 2
        )
    }
}
