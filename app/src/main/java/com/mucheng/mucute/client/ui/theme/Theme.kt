package com.mucheng.mucute.client.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Наша кастомная неоново-тёмная палитра
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC),          // Неоновый фиолетовый акцент (кнопки, иконки, ползунки)
    onPrimary = Color(0xFF121318),        // Цвет текста на фиолетовых элементах
    surface = Color(0xFF121318),          // Глубокий тёмный фон самого окна меню
    onSurface = Color(0xFFE3E2E6),        // Основной белый/светло-серый text
    surfaceContainer = Color(0xFF1A1B22), // Чуть более светлый фон для контента внутри вкладок
    onSurfaceVariant = Color(0xFFA4A3A9), // Цвет для неактивных/выключенных кнопок
    outline = Color(0xFF2D2F39)           // Цвет стильных разделителей и рамок
)

@Composable
fun MuCuteClientTheme(
    darkTheme: Boolean = true, // Принудительно включаем тёмную тему всегда
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Используем только нашу крутую тёмную схему, игнорируя стандартные цвета Android
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
