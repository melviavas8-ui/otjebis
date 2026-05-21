package com.mucheng.mucute.client.overlay

import android.os.Build
import android.view.WindowManager
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.mucheng.mucute.client.game.ModuleCategory
import com.mucheng.mucute.client.game.ModuleContent
import com.mucheng.mucute.client.ui.component.NavigationRailX

class OverlayClickGUI : OverlayWindow() {

    private val _layoutParams by lazy {
        super.layoutParams.apply {
            flags =
                flags or WindowManager.LayoutParams.FLAG_BLUR_BEHIND or WindowManager.LayoutParams.FLAG_DIM_BEHIND
            if (Build.VERSION.SDK_INT >= 31) {
                // Увеличили размытие для более мягкого и красивого эффекта матового стекла
                blurBehindRadius = 25 
            }

            layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES

            // Сделали фон чуть темнее (было 0.4f, стало 0.6f) для фокуса на чите
            dimAmount = 0.6f 
            windowAnimations = android.R.style.Animation_Dialog
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.MATCH_PARENT
        }
    }

    override val layoutParams: WindowManager.LayoutParams
        get() = _layoutParams

    private var selectedModuleCategory by mutableStateOf(ModuleCategory.Combat)

    // Создаем свою собственную красивую темную палитру
    private val CustomCheatColorScheme = darkColorScheme(
        surface = Color(0xFF121318),          // Очень глубокий темно-серый фон окна
        surfaceContainer = Color(0xFF1A1B22), // Цвет контента внутри вкладок
        primary = Color(0xFFBB86FC),          // Неоновый фиолетовый для активных иконок
        onSurface = Color(0xFFE3E2E6),        // Белый/светло-серый текст
        onSurfaceVariant = Color(0xFFA4A3A9)  // Приглушенный цвет для неактивных иконок
    )

    @Composable
    override fun Content() {
        // Оборачиваем всё в нашу кастомную тему
        MaterialTheme(colorScheme = CustomCheatColorScheme) {
            Column(
                Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        OverlayManager.dismissOverlayWindow(this)
                    },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ElevatedCard(
                    shape = MaterialTheme.shapes.large, // Сделали закругления углов чуть больше и мягче
                    modifier = Modifier
                        .padding(16.dp) // Уменьшили отступы от краев экрана, чтобы меню стало больше
                        .fillMaxSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {}
                ) {
                    Row(Modifier.fillMaxSize()) {
                        NavigationRailX(
                            windowInsets = WindowInsets(0, 0, 0, 0),
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            ModuleCategory.entries.fastForEach { moduleCategory ->
                                val isSelected = selectedModuleCategory === moduleCategory
                                NavigationRailItem(
                                    selected = isSelected,
                                    onClick = {
                                        if (selectedModuleCategory !== moduleCategory) {
                                            selectedModuleCategory = moduleCategory
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            painterResource(moduleCategory.iconResId),
                                            contentDescription = null
                                        )
                                    },
                                    label = {
                                        Text(stringResource(moduleCategory.labelResId))
                                    },
                                    alwaysShowLabel = false,
                                    // Принудительно красим элементы меню в наши неоновые цвета
                                    colors = NavigationRailItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        indicatorColor = Color(027, 021, 044, 0x33), // Едва заметный фиолетовый блик вокруг активной иконки
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }
                        VerticalDivider(color = Color(0xFF2D2F39)) // Стильный темный разделитель между боковым меню и читами
                        DialogContent()
                    }
                }
            }
        }
    }

    @Composable
    private fun DialogContent() {
        AnimatedContent(
            targetState = selectedModuleCategory,
            label = "animatedPage",
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainer)
        ) { moduleCategory ->
            Box(Modifier.fillMaxSize()) {
                ModuleContent(moduleCategory)
            }
        }
    }
}
