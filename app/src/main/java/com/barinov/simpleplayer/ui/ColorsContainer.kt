package com.barinov.simpleplayer.ui

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

data class ColorsContainer(
    val type: Type,
    val systemTopUiColor: Color,
    val navBarColor: Color? = null,
    val uiGradient: Brush? = null
){
    enum class Type{
        GRADIENT, COLOR
    }
}
