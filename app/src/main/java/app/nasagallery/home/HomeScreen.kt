package app.nasagallery.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.nasagallery.NasaGalleryTheme
import app.nasagallery.R
import app.nasagallery.common.CommonAsyncImage
import app.nasagallery.common.CommonIcon
import app.nasagallery.common.MaterialColors
import app.nasagallery.common.StringHolder
import app.nasagallery.common.VerticalGradient
import app.nasagallery.common.entity.MediaId
import app.nasagallery.common.entity.MediaTitle
import app.nasagallery.common.entity.MediaUrl
import app.nasagallery.common.get
import app.nasagallery.common.lastVisibleItem
import app.nasagallery.detail.DetailScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import org.koin.androidx.compose.koinViewModel


object HomeScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel: HomeViewModel = koinViewModel()
        val state = viewModel.viewState.collectAsStateWithLifecycle().value
        val navigator = LocalNavigator.current
        HomeScreenContent(
            state,
            { navigator?.push(DetailScreen(it)) },
            viewModel::requestData,
            viewModel::onScrollPositionChange,
        )
    }
}

@Composable
private fun HomeScreenContent(
    state: HomeUIState,
    onItemClick: (MediaId) -> Unit,
    onTryAgainClick: () -> Unit,
    onScrollPositionChange: (Int) -> Unit,
) {
    NasaGalleryTheme {
        Scaffold(
            topBar = { HomeTopBar() },
            containerColor = Color.Black
        ) { innerPadding ->

            when (state) {
                is HomeUIState.Error.Initial ->
                    ErrorContent(
                        text = state.text.string,
                        modifier = Modifier.padding(innerPadding),
                        canTryAgain = state.canTryAgain ,
                        isFullScreen = true,
                        onTryAgainClick = onTryAgainClick,
                    )

                else -> {
                    val listState = rememberLazyListState()
                    LazyColumn(
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 16.dp),
                        modifier = Modifier.padding(innerPadding),
                    ) {
                        items(state.items) { item ->
                            when (item) {
                                is MediaItem.Image -> ImageItem(
                                    item,
                                    listState,
                                    onItemClick,
                                    onScrollPositionChange,
                                )

                                is MediaItem.Skeleton -> ItemCard(item)
                                is MediaItem.Error -> ItemCard(item) {
                                    Box(Modifier.fillMaxSize()) {
                                        ErrorContent(
                                            item.text.string,
                                            item.canTryAgain,
                                            false,
                                            onTryAgainClick = onTryAgainClick,
                                            modifier = Modifier.align(Alignment.Center),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun HomeTopBar() =
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black),
        title = {
            Text(
                text = stringResource(R.string.app_name),
                fontSize = 32.sp,
                color = MaterialColors.DeepPurple[300],
                fontWeight = FontWeight(900),
            )
        },
    )

@Composable
private fun ImageItem(
    item: MediaItem.Image,
    listState: LazyListState, onItemClick: (MediaId) -> Unit,
    onScrollPositionChange: (Int) -> Unit,
) {
    val onClick = { onItemClick(item.id) }
    val lastVisible =
        remember { derivedStateOf { listState.lastVisibleItem } }
    SideEffect {
        onScrollPositionChange(lastVisible.value ?: return@SideEffect)
    }
    ItemCard(item, onClick = onClick) {
        Box {
            Image(item.imgUrl.value)
            ImageInfoColumn(Modifier.align(Alignment.BottomStart)) {
                Row {
                    Date(item.date.string)
                    Spacer(modifier = Modifier.width(8.dp))
                    if (item.isVideo) VideoBadge()
                }
                ImageTitle(item.title.value)
            }
        }
    }
}

@Composable
private fun Image(url: String, modifier: Modifier = Modifier) =
    CommonAsyncImage(
        model = url,
        modifier.fillMaxWidth(),
        contentScale = ContentScale.Crop,
        previewPlaceholderRes = R.drawable.planet
    )

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ItemCard(
    item: MediaItem,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit) = {},
    content: @Composable ColumnScope.() -> Unit = {}
) = OutlinedCard(
    colors = cardColors(containerColor = MaterialColors.Gray[900]),
    modifier = modifier
        .fillMaxWidth()
        .requiredHeight(item.height)
        .placeholder(
            item is MediaItem.Skeleton,
            MaterialColors.Gray[900],
            RoundedCornerShape(16.dp),
            PlaceholderHighlight.shimmer(MaterialColors.Gray[800]),
        ),
    border = BorderStroke(2.dp, item.highLightColor),
    onClick = onClick,
    content = content,
)

@Composable
private fun ErrorContent(
    text: String,
    canTryAgain: Boolean,
    isFullScreen: Boolean,
    modifier: Modifier = Modifier,
    onTryAgainClick: () -> Unit,
) = Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier,
) {
    val darkOrangeColor = MaterialColors.DeepOrange[900]
    val yellow = MaterialColors.Yellow[300].copy(alpha = .7f)
    if (isFullScreen) {
        Spacer(Modifier.height(96.dp))
        CommonIcon(
            R.drawable.error_outline,
            tint = darkOrangeColor.copy(alpha = .6f),
        )
    }
    Text(
        text = text,
        modifier = Modifier.padding(horizontal = 40.dp),
        fontWeight = FontWeight(500),
        fontSize = 22.sp,
        color = yellow,
        textAlign = TextAlign.Center,
    )
    if (canTryAgain) {
        Spacer(Modifier.height(if (isFullScreen) 16.dp else 4.dp))
        OutlinedButton(
            border = BorderStroke(2.dp, darkOrangeColor.copy(alpha = .2f)),
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            onClick = onTryAgainClick
        ) {
            Text(
                text = stringResource(R.string.try_again),
                fontSize = 17.sp,
                color = darkOrangeColor,
                fontWeight = FontWeight(900)
            )
        }
    }
}

@Composable
private fun ImageInfoColumn(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) = Column(
    modifier = modifier
        .fillMaxWidth()
        .background(VerticalGradient.transparentToDarkGray)
        .padding(12.dp)
        .padding(top = 56.dp),
    content = content,
)

@Composable
private fun ImageTitle(text: String, modifier: Modifier = Modifier) = Text(
    text,
    fontWeight = FontWeight(900),
    fontSize = 18.sp,
    color = MaterialColors.Red[600],
    modifier = modifier
)

@Composable
private fun Date(text: String) = Badge(text, MaterialColors.Gray[400])

@Composable
private fun VideoBadge() = Badge(stringResource(R.string.video), MaterialColors.Cyan[100])


@Composable
private fun Badge(text: String, color: Color, modifier: Modifier = Modifier) = Text(
    text,
    fontSize = 14.sp,
    color = color,
    modifier = modifier
        .border(1.dp, color.copy(alpha = .5f), RoundedCornerShape(8.dp))
        .padding(horizontal = 6.dp, vertical = 2.dp),
)

@Composable
@Preview
private fun SuccessPreview() {
    val previewState = HomeUIState.Success(
        listOf(
            MediaItem.Image(
                id = MediaId(""),
                isToday = true,
                date = StringHolder.Resource(R.string.today),
                title = MediaTitle("Title"),
                imgUrl = MediaUrl(""),
                isVideo = false,
            ),
            MediaItem.Image(
                id = MediaId(""),
                isToday = false,
                date = StringHolder.Resource(R.string.yesterday),
                title = MediaTitle("Title2"),
                imgUrl = MediaUrl(""),
                isVideo = false
            ),
            MediaItem.Image(
                id = MediaId(""),
                isToday = false,
                date = StringHolder.Value("2023-05-17"),
                title = MediaTitle("Title3"),
                imgUrl = MediaUrl(""),
                isVideo = true
            ),
        )
    )
    HomeScreenContent(previewState, {}, {}, {})
}

@Composable
@Preview
private fun InitialLoadingPreview() {
    HomeScreenContent(HomeUIState.Loading.Initial, {}, {}, {})
}

@Composable
@Preview
private fun InitialErrorPreview() {
    HomeScreenContent(
        HomeUIState.Error.Initial(StringHolder.Resource(R.string.unknown_error), true),
        {},
        {},
        {})
}
