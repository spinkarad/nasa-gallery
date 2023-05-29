package app.nasagallery.common

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object VerticalGradient {

    val transparentToDarkGray = getGradient(Color.Transparent, MaterialColors.Gray[900])
    val transparentToBlack = getGradient(Color.Transparent, Color.Black)

    private fun getGradient(start: Color, end: Color) =
        Brush.verticalGradient(listOf(start, end))

}
