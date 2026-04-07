package com.example.bookreadanddownloadforfree.bookfree.book.presentation.layoutconfig

// ui/LayoutConfig.kt

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp

// ✅ DATA CLASS UNIFICADA
@Immutable
data class LayoutConfig(
    val padding: Dp,
    val searchBarMaxWidth: Dp,
    val cornerRadius: Dp,
    val tabRowMaxWidth: Dp,
    val imageSize: Dp,
    val gridColumns: Int,
    val useTwoPane: Boolean = false,
    val smallPadding: Dp,
    val itemSpacing: Dp,
    val largePadding: Dp
)

// ✅ OBJECT COM CONFIGURAÇÕES
object LayoutConfigManager {
    @Composable
    fun rememberLayoutConfig(windowSizeClass: WindowSizeClass): LayoutConfig {
        return remember(windowSizeClass) {
            when (windowSizeClass.widthSizeClass) {
                WindowWidthSizeClass.Compact -> CompactConfig
                WindowWidthSizeClass.Medium -> MediumConfig
                WindowWidthSizeClass.Expanded -> ExpandedConfig
                else -> CompactConfig
            }
        }
    }

    // ✅ CONFIGURAÇÕES ESPECÍFICAS
    val CompactConfig = LayoutConfig(
        // Espaçamentos
        padding = 16.dp,
        smallPadding = 8.dp,
        largePadding = 24.dp,
        itemSpacing = 12.dp,
        
        searchBarMaxWidth = 400.dp,
        cornerRadius = 32.dp,
        tabRowMaxWidth = 700.dp,
        imageSize = 100.dp,
        gridColumns = 1,
        useTwoPane = false
    )

    val MediumConfig = LayoutConfig(
        //padding = 20.dp,
        searchBarMaxWidth = 500.dp,
        cornerRadius = 36.dp,
        tabRowMaxWidth = 800.dp,
        imageSize = 120.dp,
        gridColumns = 2,
        useTwoPane = false,
        // ✅ Para tablets/desktop
        padding = 20.dp,
        smallPadding = 12.dp,
        largePadding = 32.dp,
        itemSpacing = 16.dp,
    )

    val ExpandedConfig = LayoutConfig(
       // padding = 24.dp,
        searchBarMaxWidth = 600.dp,
        cornerRadius = 40.dp,
        tabRowMaxWidth = 1000.dp,
        imageSize = 140.dp,
        gridColumns = 3,
        useTwoPane = true,
        // ✅ Para tablets/desktop
        padding = 24.dp,
        smallPadding = 16.dp,
        largePadding = 40.dp,
        itemSpacing = 20.dp,
        // ✅ Para tablets/desktop
    )
}

// ✅ EXTENSION FUNCTION PARA FACILITAR USO
@Composable
fun WindowSizeClass.rememberLayoutConfig(): LayoutConfig {
    return LayoutConfigManager.rememberLayoutConfig(this)
}