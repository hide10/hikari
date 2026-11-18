package com.hide10.hikari

@JvmInline
value class Brightness private constructor(val value: Float) {
    companion object {
        val MIN = Brightness(0.05f)
        val MAX = Brightness(1f)

        fun of(value: Float): Brightness =
            if (value.isNaN()) MAX else Brightness(value.coerceIn(MIN.value, MAX.value))
    }
}

data class LightState(
    val isOn: Boolean = true,
    val brightness: Brightness = Brightness.MAX,
) {
    val windowBrightness: Float
        get() = if (isOn) brightness.value else SYSTEM_DEFAULT_BRIGHTNESS
}

private const val SYSTEM_DEFAULT_BRIGHTNESS = -1f
