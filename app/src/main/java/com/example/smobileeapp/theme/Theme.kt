package com.example.smobileeapp.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// colorPrimary를 사용하는 DarkColorScheme
private val DarkColorScheme = darkColorScheme(
    primary = colorPrimary,
    secondary = colorPrimary,
    tertiary = colorPrimary
)

// 흰색 배경, colorPrimary를 사용하는 LightColorScheme
private val LightColorScheme = lightColorScheme(
    primary = colorPrimary, // 기본 색상을 흰색으로 설정
    onPrimary = Color.Black, // 흰색 배경에 검은색 텍스트
    secondary = MyChat, // 필요에 따라 다른 색상 속성도 설정
    tertiary = AIChat,
    background = Color.White,
)

@Composable
fun GeminiChatBotTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+ but disabled here
    dynamicColor: Boolean = false, // 동적 색상 비활성화
    content: @Composable () -> Unit
) {
    // 항상 LightColorScheme 사용
    val colorScheme = LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}