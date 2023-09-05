package data.modules

import androidx.compose.animation.core.*
import data.BaseConfig
import data.LedClock
import data.LedColor
import data.LedModule
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class TimerModule(
    private val config: BaseConfig
) : LedModule {
    override val moduleId = "Timer"

    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
    private var timerJob: Job? = null

    private val _timer: MutableStateFlow<Timer> = MutableStateFlow(Timer(
        state = TimerState.STOPPED,
        duration = 60.seconds,
        percentage = 0f,
        timeLeft = 60.seconds
    ))
    val timer: StateFlow<Timer>
        get() = _timer

    private val darkLED = LedColor()
    private val darkArray = Array(config.config.ledService.ledCount) { darkLED }

    private var pulsingAlpha: Animatable<Float, AnimationVector1D>? = null

    override fun onUpdate(nanoTime: Long): Array<LedColor> {
        val timer = _timer.value

        return when (timer.state) {
            TimerState.STOPPED -> darkArray
            TimerState.RUNNING -> {
                calculateLedColors(
                    percentage = timer.percentage,
                    alpha = 1.0f,
                    timerConfig = timer.config
                )
            }
            TimerState.PAUSED -> {
                calculateLedColors(
                    percentage = timer.percentage,
                    alpha = pulsingAlpha?.value ?: 1.0f,
                    timerConfig = timer.config
                )
            }
            TimerState.FINISHED -> darkArray
        }
    }

    private fun calculateLedColors(
        percentage: Float,
        alpha: Float,
        timerConfig: TimerConfig
    ): Array<LedColor> {
        return when (timerConfig.fillType) {
            FillType.COMPLETE -> {
                calculateLedColorForRange(
                    ledList = config.getLEDs(),
                    percentage = percentage,
                    alpha = alpha,
                    fillColor = timerConfig.fillColor,
                    tailType = timerConfig.tailType
                ).map { (_, color) ->
                    color
                }.toTypedArray()
            }
            FillType.SIDES -> {
                val result = Array(config.getLEDs().size) { LedColor() }

                config.getSides().forEach { sideId ->
                    calculateLedColorForRange(
                        ledList = config.getLEDsForSide(sideId),
                        percentage = percentage,
                        alpha = alpha,
                        fillColor = timerConfig.fillColor,
                        tailType = timerConfig.tailType
                    ).forEach { (ledId, color) -> result[ledId] = color }
                }

                result
            }
        }
    }

    private fun calculateLedColorForRange(
        ledList: List<Int>,
        percentage: Float,
        alpha: Float,
        fillColor: LedColor,
        tailType: TailType
    ): Map<Int, LedColor> {
        val activeLEDs = ledList.size * percentage
        val completeLEDs = activeLEDs.toInt()
        val fractionalLED = activeLEDs - completeLEDs

        val (tailStart, tailAlphaFraction) = when (tailType) {
            TailType.FILL -> 0 to 1f
            TailType.SHORT -> completeLEDs - 10 to 0.1f
            TailType.LONG -> completeLEDs - 20 to 0.05f
        }

        return ledList.mapIndexed { i, ledId ->
            ledId to when (i) {
                in tailStart..<completeLEDs -> {
                    val tailAlpha = if (tailType == TailType.FILL) 1f else (i - tailStart) * tailAlphaFraction

                    fillColor.copy(alpha = (tailAlpha * alpha).toDouble())
                }
                completeLEDs -> fillColor.copy(alpha = (fractionalLED * alpha).toDouble())
                else -> darkLED
            }
        }.toMap()
    }

    private fun startTimerJob() {
        timerJob?.cancel()
        timerJob = scope.launch {
            while(_timer.value.state == TimerState.RUNNING) {
                _timer.update { timer ->
                    if (timer.state == TimerState.RUNNING) {
                        val now = Clock.System.now()
                        val timePassed = now - (timer.timerStarted ?: now)
                        val timeLeft =  timer.duration - timePassed

                        val timePassedMilli = timePassed.inWholeMilliseconds
                        val durationMilli = timer.duration.inWholeMilliseconds

                        val percentage = if (durationMilli == 0L) 0f else timePassedMilli.toFloat() / durationMilli.toFloat()

                        if (timeLeft.isNegative()) {
                            timer.copy(
                                state = TimerState.FINISHED
                            )
                        } else {
                            timer.copy(
                                timeLeft = timeLeft,
                                percentage = percentage
                            )
                        }
                    } else timer
                }

                delay(config.config.tableConfig.loopSleepTime)
            }
        }
    }

    fun startTimer(duration: Duration, timerConfig: TimerConfig = TimerConfig()) {
        _timer.update {
            Timer(
                state = TimerState.RUNNING,
                duration = duration,
                timeLeft = duration,
                timerStarted = Clock.System.now(),
                percentage = 0.0f,
                config = timerConfig
            )
        }
        startTimerJob()
    }

    fun resumeTimer() {
        val state = _timer.value.state

        if (state == TimerState.PAUSED) {
            _timer.update {
                it.copy(
                    state = TimerState.RUNNING,
                    timerStarted = Clock.System.now() - (it.duration - it.timeLeft),
                )
            }
            startTimerJob()
        }
    }

    fun pauseTimer() {
        _timer.update { timer ->
            if (timer.state == TimerState.RUNNING) {
                LedClock.animationScope.launch {
                    pulsingAlpha = Animatable(0.0f)
                    pulsingAlpha?.animateTo(
                        targetValue = 1.0f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(
                                durationMillis = 1000,
                                easing = Ease
                            ),
                            repeatMode = RepeatMode.Reverse
                        )
                    )
                }

                timer.copy(
                    state = TimerState.PAUSED,
                    timerStarted = null
                )
            } else {
                timer
            }
        }
    }

    fun stopTimer() {
        _timer.update {
            it.copy(
                state = TimerState.STOPPED,
                timerStarted = null
            )
        }
    }

    data class Timer(
        val state: TimerState,
        val duration: Duration,
        val timeLeft: Duration,
        val percentage: Float,
        val timerStarted: Instant? = null,
        val config: TimerConfig = TimerConfig()
    )

    data class TimerConfig(
        val fillType: FillType = FillType.COMPLETE,
        val fillColor: LedColor = LedColor(red = 1.0, green = 1.0, blue = 1.0, alpha = 1.0),
        val tailType: TailType = TailType.SHORT
    )

    enum class TimerState {
        STOPPED,
        RUNNING,
        PAUSED,
        FINISHED
    }

    enum class FillType {
        COMPLETE,
        SIDES
    }

    enum class TailType {
        FILL,
        SHORT,
        LONG
    }
}
