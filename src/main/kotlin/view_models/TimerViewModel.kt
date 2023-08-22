package view_models

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import view_models.base.ViewModel

class TimerViewModel: ViewModel() {
    private var timerJob: Job? = null

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(
        UiState(
            timerState = TimerState.Stopped
        )
    )
    val uiState: StateFlow<UiState>
        get() = _uiState

    fun resetTimer() {
        _uiState.update {
            timerJob?.cancel()

            it.copy(
                timerState = TimerState.Stopped
            )
        }
    }

    fun startTimer(seconds: Int) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            for (i in seconds downTo 0) {
                _uiState.update { it.copy(timerState = TimerState.Running(i)) }
                delay(1000L)
            }

            _uiState.update { it.copy(timerState = TimerState.Finished) }
        }
    }

    sealed class TimerState {
        data object Stopped : TimerState()

        data class Running(val secondsLeft: Int) : TimerState()

        data object Finished : TimerState()
    }

    data class UiState(
        val timerState: TimerState
    )
}