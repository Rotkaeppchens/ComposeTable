package view_models

import data.modules.TimerModule
import kotlinx.coroutines.flow.*
import view_models.base.ViewModel
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class TimerViewModel(
    private val timerModule: TimerModule
): ViewModel() {
    private val _inputDuration: MutableStateFlow<Duration> = MutableStateFlow(0.seconds)

    val uiState: StateFlow<UiState> = combine(
        _inputDuration,
        timerModule.timerState
    ) { inputDuration, state ->
        UiState(
            inputDuration = inputDuration,
            timerState = state
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UiState(
        inputDuration = 0.seconds,
        timerState = TimerModule.TimerState.Stopped
    ))

    fun setTimerDuration(duration: Duration) {
        _inputDuration.update {
            if (duration.isNegative()) 0.seconds else duration
        }
    }

    fun startTimer() = timerModule.startTimer(_inputDuration.value)
    fun resetTimer() = timerModule.resetTimer()
    fun pauseTimer() = timerModule.pauseTimer()
    fun stopTimer() = timerModule.stopTimer()

    data class UiState(
        val inputDuration: Duration,
        val timerState: TimerModule.TimerState
    )
}
