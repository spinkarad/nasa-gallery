package app.nasagallery.common

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

interface StringHolder {
    @get:Composable
    val string: String

    class Resource(@StringRes val resId: Int, private val args: List<Any>) : StringHolder {

        constructor(@StringRes resId: Int, vararg args: Any) : this(resId, args.toList())

        override val string
            @Composable get() = stringResource(resId, *args.toTypedArray())
    }

    class Value(private val value: String) : StringHolder {
        override val string @Composable get() = value
    }
}
