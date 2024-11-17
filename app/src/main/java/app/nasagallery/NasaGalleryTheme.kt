package app.nasagallery

import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RippleConfiguration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import app.nasagallery.common.MaterialColors
import app.nasagallery.common.get

@OptIn(ExperimentalMaterial3Api::class)
private val NasaGalleryRippleConfiguration =
    RippleConfiguration(
        color = MaterialColors.DeepPurple[300],
        rippleAlpha = RippleAlpha(0f, 0f, 0f, 0.3f)
    )


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NasaGalleryTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme {
        CompositionLocalProvider(
            LocalRippleConfiguration provides NasaGalleryRippleConfiguration,
            content = content
        )
    }
}

