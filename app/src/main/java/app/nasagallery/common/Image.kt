package app.nasagallery.common

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter

@Composable
@NonRestartableComposable
fun CommonAsyncImage(
    model: Any?,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    @DrawableRes previewPlaceholderRes: Int? = null,
    @DrawableRes placeholderRes: Int? = null,
    @DrawableRes fallbackRes: Int? = null,
    onSuccess: (() -> Unit)? = null,
    onError: ((AsyncImagePainter.State.Error) -> Unit)? = null,
) {

    AsyncImage(
        model = model,
        contentDescription = null,
        modifier = modifier,
        alignment = alignment,
        contentScale = contentScale,
        colorFilter = colorFilter,
        alpha = alpha,
        fallback = fallbackRes?.let { painterResource(it) },
        placeholder = getPlaceholder(previewPlaceholderRes, placeholderRes),
        onSuccess = { onSuccess?.invoke() },
        onError = onError,
    )
}

@Composable
private fun getPlaceholder(
    @DrawableRes previewImgRes: Int?,
    @DrawableRes imgRes: Int?,
) = if (LocalInspectionMode.current) {
    (previewImgRes ?: imgRes)?.let { painterResource(it) } ?: ColorPainter(Color.Magenta)
} else {
    imgRes?.let { painterResource(it) }
}
