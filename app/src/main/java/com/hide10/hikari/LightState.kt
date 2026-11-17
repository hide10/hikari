package com.hide10.hikari

/**
 * 光の画面状態の唯一の正本。
 *
 * brightness は常に [MIN_BRIGHTNESS]..[MAX_BRIGHTNESS] の範囲に収まる。
 * 範囲外の値は生成・復元のどちらの経路でも丸める。
 */
data class LightState(
    val brightness: Float = DEFAULT_BRIGHTNESS,
    val panelVisible: Boolean = true,
) {
    fun withBrightness(value: Float): LightState =
        copy(brightness = clampBrightness(value))

    fun togglePanel(): LightState = copy(panelVisible = !panelVisible)

    companion object {
        const val MIN_BRIGHTNESS = 0.05f
        const val MAX_BRIGHTNESS = 1.0f
        const val DEFAULT_BRIGHTNESS = 1.0f

        fun clampBrightness(value: Float): Float =
            value.coerceIn(MIN_BRIGHTNESS, MAX_BRIGHTNESS)

        /** 保存された明るさから初期状態を復元する。未保存なら既定値、範囲外なら丸める。 */
        fun restored(savedBrightness: Float?): LightState =
            LightState(
                brightness = savedBrightness?.let(::clampBrightness) ?: DEFAULT_BRIGHTNESS,
            )
    }
}
