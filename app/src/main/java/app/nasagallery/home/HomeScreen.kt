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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
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
import app.nasagallery.common.HorizontalSpacer
import app.nasagallery.common.ImageResource
import app.nasagallery.common.MaterialColors
import app.nasagallery.common.PreviewData
import app.nasagallery.common.StringHolder
import app.nasagallery.common.VerticalGradient
import app.nasagallery.common.VerticalSpacer
import app.nasagallery.common.alpha10
import app.nasagallery.common.alpha30
import app.nasagallery.common.alpha50
import app.nasagallery.common.alpha70
import app.nasagallery.common.entity.MediaId
import app.nasagallery.common.entity.MediaTitle
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
            containerColor = Color.Black,
        ) { innerPadding ->

            when (state) {
                is HomeUIState.Error.Initial ->
                    ErrorContent(
                        text = state.text.string,
                        modifier = Modifier.padding(innerPadding),
                        canTryAgain = state.canTryAgain,
                        isFullScreen = true,
                        onTryAgainClick = onTryAgainClick,
                    )

                else -> {
                    val listState = rememberLazyListState()

                    val contentPadding = PaddingValues(
                        16.dp,
                        innerPadding.calculateTopPadding(),
                        16.dp,
                        16.dp
                    )
                    LazyColumn(
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = contentPadding,
                    ) {
                        items(state.items) { item ->
                            when (item) {
                                is GalleryItem.Media -> ImageItem(
                                    item,
                                    listState,
                                    onItemClick,
                                    onScrollPositionChange,
                                )

                                is GalleryItem.Skeleton -> ItemCard(item)
                                is GalleryItem.Error -> ItemCard(item) {
                                    ErrorContent(
                                        item.text.string,
                                        item.canTryAgain,
                                        false,
                                        onTryAgainClick = onTryAgainClick,
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

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun HomeTopBar() =
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black.alpha70),
        title = {
            Text(
                text = stringResource(R.string.app_name),
                fontSize = 32.sp,
                color = MaterialColors.DeepPurple[100],
                fontWeight = FontWeight(900),
            )
        },
    )

@Composable
private fun ImageItem(
    item: GalleryItem.Media,
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
            Image(item.imageResource)
            ImageInfoColumn(Modifier.align(Alignment.BottomStart)) {
                Row {
                    Date(item.date.string)
                    HorizontalSpacer(8.dp)
                    if (item.isVideo) VideoBadge()
                }
                ImageTitle(item.title.value)
            }
        }
    }
}

@Composable
private fun Image(imageResource: ImageResource, modifier: Modifier = Modifier) =
    CommonAsyncImage(
        model = imageResource.model,
        modifier.fillMaxWidth(),
        contentScale = ContentScale.Crop,
        previewPlaceholderRes = R.drawable.ic_planet
    )

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ItemCard(
    item: GalleryItem,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit) = {},
    content: @Composable ColumnScope.() -> Unit = {}
) = OutlinedCard(
    colors = cardColors(containerColor = MaterialColors.Gray[900]),
    modifier = modifier
        .fillMaxWidth()
        .requiredHeight(item.height)
        .placeholder(
            item is GalleryItem.Skeleton,
            MaterialColors.Gray[900],
            RoundedCornerShape(16.dp),
            PlaceholderHighlight.shimmer(MaterialColors.Gray[800]),
        ),
    border = BorderStroke(1.dp, item.highLightColor),
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
) = Column(modifier = modifier.fillMaxSize()) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.weight(1f),
    ) {
        val textColor = MaterialColors.Gray[400]
        val warningColor = MaterialColors.Red[800]
        if (isFullScreen) {
            Box(
                Modifier.background(warningColor.alpha10, RoundedCornerShape(20.dp))
            ) {
                CommonIcon(
                    R.drawable.ic_warning,
                    tint = warningColor.alpha30,
                    modifier = Modifier
                        .padding(16.dp)
                        .requiredSize(56.dp)
                )
            }
            VerticalSpacer(16.dp)
        }
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 40.dp),
            fontWeight = FontWeight(500),
            fontSize = 20.sp,
            color = textColor,
            textAlign = TextAlign.Center,
        )
        if (canTryAgain) {
            VerticalSpacer(if (isFullScreen) 16.dp else 4.dp)
            OutlinedButton(
                border = BorderStroke(1.dp, textColor.alpha30),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                onClick = onTryAgainClick
            ) {
                Text(
                    text = stringResource(R.string.try_again),
                    fontSize = 17.sp,
                    color = textColor,
                    fontWeight = FontWeight(900)
                )
            }
        }
    }
    if (isFullScreen) Spacer(Modifier.weight(0.5f))
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
        .padding(top = 48.dp),
    content = content,
)

@Composable
private fun ImageTitle(text: String, modifier: Modifier = Modifier) = Text(
    text,
    fontWeight = FontWeight(500),
    fontSize = 17.sp,
    color = MaterialColors.DeepPurple[100],
    modifier = modifier
)

@Composable
private fun Date(text: String) = Badge(text, MaterialColors.Gray[200])

@Composable
private fun VideoBadge() = Badge(stringResource(R.string.video), MaterialColors.Cyan[100])


@Composable
private fun Badge(text: String, color: Color, modifier: Modifier = Modifier) = Text(
    text,
    fontSize = 14.sp,
    color = color,
    modifier = modifier
        .border(1.dp, color.alpha50, RoundedCornerShape(8.dp))
        .padding(horizontal = 6.dp, vertical = 2.dp),
)

@Composable
@Preview
private fun SuccessPreview() {
    val previewState = HomeUIState.Success(
        listOf(
            GalleryItem.Media(
                id = MediaId(""),
                isToday = true,
                date = StringHolder.Resource(R.string.today),
                title = MediaTitle("Title"),
                imageResource = PreviewData.image,
                isVideo = false,
            ),
            GalleryItem.Media(
                id = MediaId(""),
                isToday = false,
                date = StringHolder.Resource(R.string.yesterday),
                title = MediaTitle("Title2"),
                imageResource = PreviewData.image,
                isVideo = false
            ),
            GalleryItem.Media(
                id = MediaId(""),
                isToday = false,
                date = StringHolder.Value("2023-05-17"),
                title = MediaTitle("Title3"),
                imageResource = PreviewData.image,
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
private fun SequentialErrorPreview() {
    HomeScreenContent(HomeUIState.Loading.Sequential(
        listOf(
            GalleryItem.Media(
                id = MediaId(""),
                isToday = false,
                date = StringHolder.Resource(R.string.yesterday),
                title = MediaTitle("Title2"),
                imageResource = PreviewData.image,
                isVideo = false
            ),
            GalleryItem.Error(StringHolder.Resource(R.string.unknown_error), true)
        )
    ), {}, {}, {})
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
