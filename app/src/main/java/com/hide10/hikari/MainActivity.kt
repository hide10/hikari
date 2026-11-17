package com.hide10.hikari

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val LightSurface = Color(0xFFFFF6E8)

/**
 * 画面全体を光らせる単一 Activity。
 * 明るさはウィンドウ属性 (screenBrightness) でのみ適用し、端末のシステム設定は変更しない。
 * フォアグラウンドを離れればウィンドウ自体が隠れるため、明るさは自動的にシステム値へ戻る。
 */
class MainActivity : ComponentActivity() {

    private val viewModel: LightViewModel by viewModels {
        LightViewModel.factory(SharedPreferencesBrightnessStore(applicationContext))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContent {
            MaterialTheme(colorScheme = lightColorScheme()) {
                LightScreen(viewModel)
            }
        }
    }

    @Composable
    private fun LightScreen(viewModel: LightViewModel) {
        val state = viewModel.state

        // 状態の明るさをウィンドウ属性へ反映する。状態が唯一の正本で、ここは写すだけ。
        val targetWindow = window
        SideEffect {
            val attrs = targetWindow.attributes
            attrs.screenBrightness = state.brightness
            targetWindow.attributes = attrs
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LightSurface)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { viewModel.togglePanel() },
        ) {
            if (state.panelVisible) {
                ControlPanel(
                    state = state,
                    onBrightnessChange = viewModel::setBrightness,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .safeDrawingPadding(),
                )
            }
        }
    }

    @Composable
    private fun ControlPanel(
        state: LightState,
        onBrightnessChange: (Float) -> Unit,
        modifier: Modifier = Modifier,
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
                // パネル上のタップは背面の表示トグルへ伝播させない
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {},
            tonalElevation = 2.dp,
        ) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                Text(text = "明るさ ${(state.brightness * 100).toInt()}%")
                Slider(
                    value = state.brightness,
                    onValueChange = onBrightnessChange,
                    valueRange = LightState.MIN_BRIGHTNESS..LightState.MAX_BRIGHTNESS,
                )
            }
        }
    }
}
