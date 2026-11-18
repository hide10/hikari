package com.hide10.hikari

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val store = BrightnessStore(this)
        setContent {
            var state by rememberSaveable(stateSaver = LightStateSaver) {
                mutableStateOf(LightState(brightness = store.load()))
            }
            SideEffect {
                window.attributes = window.attributes.apply { screenBrightness = state.windowBrightness }
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightStatusBars = state.isOn
                    isAppearanceLightNavigationBars = state.isOn
                }
            }
            LightScreen(
                state = state,
                onIsOnChange = { state = state.copy(isOn = it) },
                onBrightnessChange = { state = state.copy(brightness = Brightness.of(it)) },
                onBrightnessChangeFinished = { store.save(state.brightness) },
            )
        }
    }
}

private val LightStateSaver = listSaver<LightState, Any>(
    save = { listOf(it.isOn, it.brightness.value) },
    restore = { LightState(isOn = it[0] as Boolean, brightness = Brightness.of(it[1] as Float)) },
)

@Composable
private fun LightScreen(
    state: LightState,
    onIsOnChange: (Boolean) -> Unit,
    onBrightnessChange: (Float) -> Unit,
    onBrightnessChangeFinished: () -> Unit,
) {
    MaterialTheme(colorScheme = darkColorScheme()) {
        Box(
            Modifier
                .fillMaxSize()
                .background(if (state.isOn) Color.White else Color.Black),
        ) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .safeDrawingPadding()
                    .padding(16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF2B2B2B),
                contentColor = Color.White,
            ) {
                Column(Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(if (state.isOn) R.string.light_on else R.string.light_off),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f),
                        )
                        val switchDescription = stringResource(R.string.light_switch)
                        Switch(
                            checked = state.isOn,
                            onCheckedChange = onIsOnChange,
                            modifier = Modifier.semantics { contentDescription = switchDescription },
                        )
                    }
                    val sliderDescription = stringResource(R.string.brightness)
                    Slider(
                        value = state.brightness.value,
                        onValueChange = onBrightnessChange,
                        onValueChangeFinished = onBrightnessChangeFinished,
                        valueRange = Brightness.MIN.value..Brightness.MAX.value,
                        modifier = Modifier.semantics { contentDescription = sliderDescription },
                    )
                }
            }
        }
    }
}
