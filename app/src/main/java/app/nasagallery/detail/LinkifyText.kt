package app.nasagallery.detail

import android.os.Build
import android.text.SpannableString
import android.text.style.URLSpan
import android.text.util.Linkify
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit

/**
 * Source https://github.com/firefinchdev/linkify-text
 * The solution was extended to support animating (changing color) of link when it is pressed.
 */
@Composable
fun LinkifyText(
    text: String,
    modifier: Modifier = Modifier,
    linkColor: Color = Color.Unspecified,
    activatedLinkColor: Color = Color.Unspecified,
    activatedLinkBgColor: Color = Color.Unspecified,
    linkEntire: Boolean = false,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
    clickable: Boolean = true,
    onClickLink: ((linkText: String) -> Unit)? = null,
) {
    val uriHandler = LocalUriHandler.current
    val linkInfos =
        if (linkEntire) {
            listOf(LinkInfo(text, 0, text.length))
        } else {
            SpannableStr.getLinkInfos(text)
        }
    val activatedSectionStart = remember { mutableStateOf<Int?>(null) }
    val annotatedString = buildAnnotatedString {
        append(text)
        linkInfos.forEach {
            val isActivated = activatedSectionStart.value == it.start
            val actualLinkColor = if (isActivated) activatedLinkColor else linkColor
            val bgColor = if (isActivated) activatedLinkBgColor else Color.Unspecified

            val clampedStart = it.start.coerceIn(text.indices)
            val clampedEnd = it.end.coerceIn(text.indices)

            addStyle(
                style = SpanStyle(
                    color = actualLinkColor,
                    textDecoration = TextDecoration.Underline,
                    background = bgColor
                ),
                start = clampedStart,
                end = clampedEnd
            )
            addStringAnnotation(
                tag = "tag",
                annotation = it.url,
                start = clampedStart,
                end = clampedEnd
            )
        }
    }
    if (clickable) {
        ClickableText(
            text = annotatedString,
            modifier = modifier,
            color = color,
            fontSize = fontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            letterSpacing = letterSpacing,
            textDecoration = textDecoration,
            textAlign = textAlign,
            lineHeight = lineHeight,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            onTextLayout = onTextLayout,
            style = style,
            onClick = { offset ->
                annotatedString.getStringAnnotations(
                    start = offset,
                    end = offset,
                ).firstOrNull()?.let { result ->
                    if (linkEntire) {
                        onClickLink?.invoke(annotatedString.substring(result.start, result.end))
                    } else {
                        uriHandler.openUri(result.item)
                        onClickLink?.invoke(annotatedString.substring(result.start, result.end))
                    }
                }
            },
            onPressedSectionChanged = { offset ->
                val section =
                    offset?.let { annotatedString.getStringAnnotations(start = it, end = it) }
                        ?.firstOrNull()?.start
                activatedSectionStart.value = section
            }
        )
    } else {
        Text(
            text = annotatedString,
            modifier = modifier,
            color = color,
            fontSize = fontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            letterSpacing = letterSpacing,
            textDecoration = textDecoration,
            textAlign = textAlign,
            lineHeight = lineHeight,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            onTextLayout = onTextLayout,
            style = style
        )
    }
}

@Composable
private fun ClickableText(
    text: AnnotatedString,
    onClick: (Int) -> Unit,
    onPressedSectionChanged: (Int?) -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
) {
    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
    val pressIndicator = modifier.pointerInput(Unit) {
        detectTapGestures(
            onPress = { offset ->
                val position = layoutResult.value?.getOffsetForPosition(offset)
                onPressedSectionChanged(position)
                val hasClicked = tryAwaitRelease()
                onPressedSectionChanged(null)
                if (hasClicked) {
                    position?.let { onClick(it) }
                }
            }
        )
    }
    Text(
        text = text,
        modifier = pressIndicator,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        onTextLayout = {
            layoutResult.value = it
            onTextLayout(it)
        },
        style = style
    )
}

private data class LinkInfo(
    val url: String,
    val start: Int,
    val end: Int,
)

private class SpannableStr(source: CharSequence) : SpannableString(source) {

    private data class Data(
        val what: Any?,
        val start: Int,
        val end: Int,
    )

    private val spanList = mutableListOf<Data>()

    private val linkInfos: List<LinkInfo>
        get() = spanList.filter { it.what is URLSpan }.map {
            LinkInfo(
                (it.what as URLSpan).url,
                it.start,
                it.end
            )
        }

    override fun removeSpan(what: Any?) {
        super.removeSpan(what)
        spanList.removeAll { it.what == what }
    }

    override fun setSpan(what: Any?, start: Int, end: Int, flags: Int) {
        super.setSpan(what, start, end, flags)
        spanList.add(Data(what, start, end))
    }

    companion object {
        fun getLinkInfos(text: String): List<LinkInfo> {
            val spannableStr = SpannableStr(text)
            Linkify.addLinks(spannableStr, Linkify.WEB_URLS) { str: String -> URLSpan(str) }
            return spannableStr.linkInfos
        }
    }
}

data class LinkSpan(
    val url: String,
    val start: Int,
    val end: Int,
)

