package com.hide10.hikari

import android.content.Context

/** 最後に使った明るさの保存先。テストではフェイクに差し替える。 */
interface BrightnessStore {
    fun load(): Float?
    fun save(brightness: Float)
}

class SharedPreferencesBrightnessStore(context: Context) : BrightnessStore {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun load(): Float? =
        if (prefs.contains(KEY_BRIGHTNESS)) {
            prefs.getFloat(KEY_BRIGHTNESS, LightState.DEFAULT_BRIGHTNESS)
        } else {
            null
        }

    override fun save(brightness: Float) {
        prefs.edit().putFloat(KEY_BRIGHTNESS, brightness).apply()
    }

    private companion object {
        const val PREFS_NAME = "hikari"
        const val KEY_BRIGHTNESS = "brightness"
    }
}
