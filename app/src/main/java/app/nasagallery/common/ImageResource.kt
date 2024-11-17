package app.nasagallery.common

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import app.nasagallery.common.entity.MediaUrl

sealed class ImageResource(val model: Any) {

    data class Drawable(@DrawableRes val drawableRes: Int) : ImageResource(drawableRes)
    data class Url(val url: String) : ImageResource(url)
}
