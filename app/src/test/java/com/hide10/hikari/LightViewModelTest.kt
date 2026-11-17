package com.hide10.hikari

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LightViewModelTest {

    private class FakeBrightnessStore(var saved: Float? = null) : BrightnessStore {
        override fun load(): Float? = saved
        override fun save(brightness: Float) {
            saved = brightness
        }
    }

    @Test
    fun `保存された明るさを復元する`() {
        val viewModel = LightViewModel(FakeBrightnessStore(saved = 0.4f))
        assertEquals(0.4f, viewModel.state.brightness, 0.0001f)
    }

    @Test
    fun `保存がなければ既定の明るさで始まる`() {
        val viewModel = LightViewModel(FakeBrightnessStore(saved = null))
        assertEquals(LightState.DEFAULT_BRIGHTNESS, viewModel.state.brightness, 0.0001f)
    }

    @Test
    fun `範囲外の保存値は丸めて復元する`() {
        val tooLow = LightViewModel(FakeBrightnessStore(saved = 0.0f))
        assertEquals(LightState.MIN_BRIGHTNESS, tooLow.state.brightness, 0.0001f)
        val tooHigh = LightViewModel(FakeBrightnessStore(saved = 2.5f))
        assertEquals(LightState.MAX_BRIGHTNESS, tooHigh.state.brightness, 0.0001f)
    }

    @Test
    fun `明るさの変更は範囲内に丸められ保存される`() {
        val store = FakeBrightnessStore()
        val viewModel = LightViewModel(store)

        viewModel.setBrightness(0.01f)
        assertEquals(LightState.MIN_BRIGHTNESS, viewModel.state.brightness, 0.0001f)
        assertEquals(LightState.MIN_BRIGHTNESS, store.saved)

        viewModel.setBrightness(1.5f)
        assertEquals(LightState.MAX_BRIGHTNESS, viewModel.state.brightness, 0.0001f)
        assertEquals(LightState.MAX_BRIGHTNESS, store.saved)

        viewModel.setBrightness(0.5f)
        assertEquals(0.5f, viewModel.state.brightness, 0.0001f)
        assertEquals(0.5f, store.saved)
    }

    @Test
    fun `パネルのトグルは表示だけを切り替え明るさは変えない`() {
        val viewModel = LightViewModel(FakeBrightnessStore(saved = 0.6f))
        assertTrue(viewModel.state.panelVisible)

        viewModel.togglePanel()
        assertFalse(viewModel.state.panelVisible)
        assertEquals(0.6f, viewModel.state.brightness, 0.0001f)

        viewModel.togglePanel()
        assertTrue(viewModel.state.panelVisible)
    }
}
