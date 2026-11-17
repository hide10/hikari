package com.hide10.hikari

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/** [LightState] を所有し、明るさの変更を [BrightnessStore] へ永続化する。 */
class LightViewModel(private val store: BrightnessStore) : ViewModel() {

    var state: LightState by mutableStateOf(LightState.restored(store.load()))
        private set

    fun setBrightness(value: Float) {
        state = state.withBrightness(value)
        store.save(state.brightness)
    }

    fun togglePanel() {
        state = state.togglePanel()
    }

    companion object {
        fun factory(store: BrightnessStore): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    require(modelClass.isAssignableFrom(LightViewModel::class.java))
                    return LightViewModel(store) as T
                }
            }
    }
}
