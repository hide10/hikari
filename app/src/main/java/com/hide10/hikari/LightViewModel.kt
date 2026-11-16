package com.hide10.hikari

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LightUiState(
    val brightness: Float = DEFAULT_BRIGHTNESS,
    val uiVisible: Boolean = true,
) {
    companion object {
        // 0.0 は BRIGHTNESS_OVERRIDE_OFF（画面が消える）になるため、下限は 0.01 とする。
        const val MIN_BRIGHTNESS = 0.01f
        const val MAX_BRIGHTNESS = 1.0f
        const val DEFAULT_BRIGHTNESS = 1.0f

        fun clampBrightness(value: Float): Float =
            value.coerceIn(MIN_BRIGHTNESS, MAX_BRIGHTNESS)
    }
}

class LightViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LightUiState())
    val uiState: StateFlow<LightUiState> = _uiState.asStateFlow()

    fun onBrightnessChanged(value: Float) {
        _uiState.update { it.copy(brightness = LightUiState.clampBrightness(value)) }
    }

    fun onToggleUi() {
        _uiState.update { it.copy(uiVisible = !it.uiVisible) }
    }
}
