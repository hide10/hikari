package com.hide10.hikari

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LightStateTest {

    @Test
    fun brightnessKeepsValueInRange() {
        assertEquals(0.5f, Brightness.of(0.5f).value, 0f)
        assertEquals(0.05f, Brightness.of(0.05f).value, 0f)
        assertEquals(1f, Brightness.of(1f).value, 0f)
    }

    @Test
    fun brightnessBelowMinimumBecomesMinimum() {
        assertEquals(0.05f, Brightness.of(0f).value, 0f)
        assertEquals(0.05f, Brightness.of(-3f).value, 0f)
        assertEquals(0.05f, Brightness.of(Float.NEGATIVE_INFINITY).value, 0f)
    }

    @Test
    fun brightnessAboveMaximumBecomesMaximum() {
        assertEquals(1f, Brightness.of(1.5f).value, 0f)
        assertEquals(1f, Brightness.of(Float.POSITIVE_INFINITY).value, 0f)
    }

    @Test
    fun brightnessFromNaNIsMaximum() {
        assertEquals(1f, Brightness.of(Float.NaN).value, 0f)
    }

    @Test
    fun defaultStateIsOnAtFullBrightness() {
        val state = LightState()

        assertTrue(state.isOn)
        assertEquals(1f, state.windowBrightness, 0f)
    }

    @Test
    fun onStateUsesChosenBrightness() {
        val state = LightState(isOn = true, brightness = Brightness.of(0.3f))

        assertEquals(0.3f, state.windowBrightness, 0f)
    }

    @Test
    fun offStateHandsBrightnessBackToSystem() {
        val state = LightState(isOn = false, brightness = Brightness.of(0.3f))

        assertEquals(-1f, state.windowBrightness, 0f)
    }
}
