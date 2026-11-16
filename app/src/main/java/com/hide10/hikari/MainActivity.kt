package com.hide10.hikari

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // コンテンツをシステムバー・カットアウトの領域まで広げる。
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // 表示中は画面を消灯させない。
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        // カットアウト領域まで塗りつぶす。
        val cutoutMode = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R ->
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ->
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            else -> null
        }
        if (cutoutMode != null) {
            window.attributes = window.attributes.apply {
                layoutInDisplayCutoutMode = cutoutMode
            }
        }
        hideSystemBars()
        setContent {
            MaterialTheme {
                HikariScreen()
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemBars()
    }

    private fun hideSystemBars() {
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

@Composable
fun HikariScreen(viewModel: LightViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // 明るさは Window 属性に毎回反映する（構成変更後の再適用も兼ねる）。
    SideEffect {
        val window = (context as? Activity)?.window ?: return@SideEffect
        window.attributes = window.attributes.apply {
            screenBrightness = state.brightness
        }
    }

    var showInfoDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { viewModel.onToggleUi() },
    ) {
        if (state.uiVisible) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.55f), RoundedCornerShape(16.dp))
                    // パネル内の余白などのタップはここで消費し、背面の表示切替に届かせない。
                    .pointerInput(Unit) { detectTapGestures { } }
                    .padding(horizontal = 20.dp, vertical = 12.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "明るさ ${(state.brightness * 100).roundToInt()}%",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f),
                    )
                    IconButton(onClick = { showInfoDialog = true }) {
                        Text("i", color = Color.White, style = MaterialTheme.typography.titleLarge)
                    }
                }
                Slider(
                    value = state.brightness,
                    onValueChange = { viewModel.onBrightnessChanged(it) },
                    valueRange = LightUiState.MIN_BRIGHTNESS..LightUiState.MAX_BRIGHTNESS,
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color.White,
                        inactiveTrackColor = Color.White.copy(alpha = 0.4f),
                    ),
                )
            }
        }
    }

    if (showInfoDialog) {
        val versionName = remember(context) {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "-"
        }
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = { Text("光") },
            text = {
                Column {
                    Text("バージョン: $versionName")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("画面全体を、選んだ明るさで光らせるアプリです。")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("操作方法:", style = MaterialTheme.typography.labelLarge)
                    Text("・スライダーで明るさを調整")
                    Text("・画面をタップすると操作パネルを表示／非表示")
                    Text("・アプリを終了すると、端末の明るさに戻ります")
                }
            },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text("閉じる")
                }
            },
        )
    }
}
