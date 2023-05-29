package app.nasagallery.common

import androidx.annotation.DrawableRes
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource

@Composable
fun CommonIcon(
    @DrawableRes resId: Int,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
) = Icon(
        painterResource(resId),
        modifier = modifier,
        contentDescription = null,
        tint = tint,
    )
