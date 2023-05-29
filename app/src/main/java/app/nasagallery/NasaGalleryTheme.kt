package app.nasagallery

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import app.nasagallery.common.MaterialColors
import app.nasagallery.common.get

@Composable
fun NasaGalleryTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme {
        CompositionLocalProvider(
            LocalRippleTheme provides NasaGalleryRippleTheme,
            content = content
        )
    }
}


private object NasaGalleryRippleTheme : RippleTheme {

    @Composable
    override fun defaultColor() =
        RippleTheme.defaultRippleColor(
            MaterialColors.Red[600],
            lightTheme = true
        )

    @Composable
    override fun rippleAlpha(): RippleAlpha =
        RippleTheme.defaultRippleAlpha(
            MaterialColors.Red[600].copy(0.3f),
            lightTheme = true
        )
}
