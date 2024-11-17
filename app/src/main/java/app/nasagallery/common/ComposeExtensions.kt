package app.nasagallery.common

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

val LazyListState.lastVisibleItem: Int?
    get() = layoutInfo.visibleItemsInfo.lastOrNull()?.index

@Composable
inline fun Modifier.modifyIf(condition: Boolean, ifTrue: @Composable Modifier.() -> Modifier) =
    if (condition) ifTrue() else this