package com.hide10.hikari

import android.content.Context

class BrightnessStore(context: Context) {
    private val prefs = context.getSharedPreferences("light", Context.MODE_PRIVATE)

    fun load(): Brightness = Brightness.of(prefs.getFloat(KEY, Brightness.MAX.value))

    fun save(brightness: Brightness) {
        prefs.edit().putFloat(KEY, brightness.value).apply()
    }
}

private const val KEY = "brightness"
