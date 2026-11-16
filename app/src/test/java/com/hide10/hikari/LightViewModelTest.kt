package com.hide10.hikari

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LightViewModelTest {

    @Test
    fun initialState_isFullBrightnessAndUiVisible() {
        val viewModel = LightViewModel()
        val state = viewModel.uiState.value
        assertEquals(LightUiState.DEFAULT_BRIGHTNESS, state.brightness)
        assertTrue(state.uiVisible)
    }

    @Test
    fun brightnessChanged_withinRange_isAppliedAsIs() {
        val viewModel = LightViewModel()
        viewModel.onBrightnessChanged(0.5f)
        assertEquals(0.5f, viewModel.uiState.value.brightness)
    }

    @Test
    fun brightnessChanged_aboveMax_isClampedToMax() {
        val viewModel = LightViewModel()
        viewModel.onBrightnessChanged(1.5f)
        assertEquals(LightUiState.MAX_BRIGHTNESS, viewModel.uiState.value.brightness)
    }

    @Test
    fun brightnessChanged_zeroOrBelow_isClampedToMin() {
        val viewModel = LightViewModel()
        viewModel.onBrightnessChanged(0f)
        assertEquals(LightUiState.MIN_BRIGHTNESS, viewModel.uiState.value.brightness)
        viewModel.onBrightnessChanged(-1f)
        assertEquals(LightUiState.MIN_BRIGHTNESS, viewModel.uiState.value.brightness)
    }

    @Test
    fun toggleUi_flipsVisibility() {
        val viewModel = LightViewModel()
        viewModel.onToggleUi()
        assertFalse(viewModel.uiState.value.uiVisible)
        viewModel.onToggleUi()
        assertTrue(viewModel.uiState.value.uiVisible)
    }
}
