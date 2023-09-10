package data.modules

import androidx.compose.animation.core.*
import data.BaseConfig
import data.LedAnimationClock
import data.LedColor
import data.LedModule
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.math.exp
import kotlin.math.sin
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class TimerModule(
    private val config: BaseConfig,
    private val ledAnimClock: LedAnimationClock
) : LedModule() {
    override val moduleId = "Timer"

    private var timerJob: Job? = null

    private val _timer: MutableStateFlow<Timer> = MutableStateFlow(Timer(
        state = TimerState.STOPPED,
        duration = 60.seconds,
        percentage = 0f,
        timeLeft = 60.seconds
    ))
    val timer: StateFlow<Timer>
        get() = _timer

    private val darkArray = Array(config.config.ledService.ledCount) { LedColor.Dark }

    private var pulsingAlpha: Animatable<Float, AnimationVector1D>? = null
    private val finishingAlpha = Animatable(0.0f)

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
            TimerState.FINISHED -> {
                val color = timer.config.fillColor.copy(alpha = finishingAlpha.value.toDouble())

                Array(config.config.ledService.ledCount) { color }
            }
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
                val result = Array(config.getLEDs().size) { LedColor.Dark }

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

        val tailRange: ClosedFloatingPointRange<Double> = when (tailType) {
            TailType.FILL -> 0.0
            TailType.SHORT -> activeLEDs - 10.0
            TailType.LONG -> activeLEDs - 20.0
        }..activeLEDs + 10.0

        return ledList.mapIndexed { i, ledId ->
            val result = if (i.toDouble() in tailRange) {
                when {
                    i > activeLEDs -> {
                        val tailLedId = i.toDouble() - activeLEDs
                        val ledTailPos = tailLedId / (tailRange.endInclusive - activeLEDs)

                        exp(-6.0 * ledTailPos)
                    }
                    tailType == TailType.FILL -> 1.0
                    else -> {
                        val tailLedId = i.toDouble() - tailRange.start
                        val ledTailPos = tailLedId / (activeLEDs - tailRange.start)

                        sin((Math.PI / 2) * ledTailPos)
                    }
                }
            } else {
                0.0
            }.coerceIn(0.0, 1.0)

            ledId to fillColor.copy(alpha = result * alpha)
        }.toMap()
    }

    private fun startTimerJob() {
        timerJob?.cancel()
        timerJob = moduleScope.launch {
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
                            ledAnimClock.animationScope.launch {
                                finishingAlpha.snapTo(
                                    if (timer.config.tailType == TailType.FILL) 1.0f
                                    else 0.0f
                                )
                                finishingAlpha.animateTo(
                                    targetValue = 0.0f,
                                    animationSpec = keyframes {
                                        durationMillis = 2400

                                        1.0f at 400
                                        0.0f at 800
                                        1.0f at 1200
                                        0.0f at 1600
                                        1.0f at 2000
                                        0.0f at 2400
                                    }
                                )
                            }

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
                ledAnimClock.animationScope.launch {
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
