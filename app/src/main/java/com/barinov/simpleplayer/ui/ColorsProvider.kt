package com.barinov.simpleplayer.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.barinov.simpleplayer.ui.theme.primary_color

object ColorsProvider {


    fun obtainDefaultLight(): ColorsContainer {
        return ColorsContainer(
            ColorsContainer.Type.COLOR,
            systemTopUiColor = Color(0xDDE6E0E2),
        )
    }

    fun obtainDefaultDark(): ColorsContainer {
        return ColorsContainer(
            ColorsContainer.Type.COLOR,
            Color(0xDD242323),
        )
    }

    fun obtainOnScanScreen(): ColorsContainer{
        val end = Offset(0f, Float.POSITIVE_INFINITY)
        val colors: List<Color> = listOf(
            Color(0xDDDADFD8),
            Color(0xDDBEC9B9),
            Color(0xDDBEE2B4),
            Color(0xDDB1DFA4),
            Color(0xDDA9E498),
        )
        return ColorsContainer(
            ColorsContainer.Type.GRADIENT,
            systemTopUiColor = Color(0xDDDADFD8),
            navBarColor = Color(0xDDA9E498),
            uiGradient = Brush.linearGradient(
                colors = colors,
                start = Offset.Zero,
                end = end
            )
        )

    }
    fun obtainOnHomeScreen(): ColorsContainer {
        val end = Offset(0f, Float.POSITIVE_INFINITY)
        val colors: List<Color> = listOf(
            Color(0xDD8EDF7C),
            Color(0xDD81C971),
            Color(0xDD6EAF5F),
            Color(0xDD65A058),
            Color(0xDD619656),
        )
        return ColorsContainer(
            ColorsContainer.Type.GRADIENT,
            systemTopUiColor = Color(0xDD8EDF7C),
            navBarColor = Color(0xDD619656),
            uiGradient = Brush.linearGradient(
                colors = colors,
                start = Offset.Zero,
                end = end
            )
        )
    }

    fun obtainOnFileBrowserLight(): ColorsContainer {
        return ColorsContainer(
            ColorsContainer.Type.COLOR,
//            systemTopUiColor = primary_color,
            systemTopUiColor = Color(0xDD98C288),
            navBarColor = Color(0xDD98C288),
        )
    }
}