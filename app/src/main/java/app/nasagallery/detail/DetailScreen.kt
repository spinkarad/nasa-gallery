package app.nasagallery.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.nasagallery.NasaGalleryTheme
import app.nasagallery.R
import app.nasagallery.common.CommonAsyncImage
import app.nasagallery.common.CommonIcon
import app.nasagallery.common.MaterialColors
import app.nasagallery.common.VerticalGradient
import app.nasagallery.common.entity.MediaExplanation
import app.nasagallery.common.entity.MediaId
import app.nasagallery.common.entity.MediaTitle
import app.nasagallery.common.entity.MediaUrl
import app.nasagallery.common.get
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

data class DetailScreen(val id: MediaId) : Screen {
    @Composable
    override fun Content() {
        val viewModel: DetailViewModel = koinViewModel(parameters = { parametersOf(id) })
        DetailScreenContent(viewModel.media ?: return)
    }
}

@Composable
private fun DetailScreenContent(state: DetailUIState) {
    NasaGalleryTheme {
        Scaffold(
            topBar = { DetailTopBar(state.title.value) },
            containerColor = Color.Black
        ) { innerPadding ->
            DetailColumn(Modifier.padding(innerPadding)) {
                Box {
                    Image(state.url.value)
                    GradientBox(Modifier.align(Alignment.BottomCenter))
                }
                Explanation(state.explanation.value)
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DetailTopBar(text: String) {
    val navigator = LocalNavigator.current
    LargeTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Black,
            navigationIconContentColor = MaterialColors.Red[600],
        ),
        navigationIcon = {
            IconButton(onClick = { navigator?.pop() }) {
                CommonIcon(R.drawable.arrow_back)
            }
        },
        title = {
            Text(
                text = text,
                fontSize = 32.sp,
                color = MaterialColors.Red[600],
                fontWeight = FontWeight(900),
            )
        },
    )
}

@Composable
private fun DetailColumn(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) = Column(
    modifier = modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState()),
    content = content,
)

@Composable
private fun Image(url: String, modifier: Modifier = Modifier) =
    CommonAsyncImage(
        model = url,
        modifier.fillMaxWidth(),
        contentScale = ContentScale.FillWidth,
        previewPlaceholderRes = R.drawable.planet
    )

@Composable
private fun Explanation(text: String, modifier: Modifier = Modifier) = Text(
    text = text,
    fontSize = 15.sp,
    color = MaterialColors.Gray[600],
    modifier = modifier
        .padding(horizontal = 16.dp)
        .padding(bottom = 16.dp)
)

@Composable
private fun GradientBox(modifier: Modifier = Modifier) = Box(
    modifier = modifier
        .requiredHeight(48.dp)
        .fillMaxWidth()
        .background(VerticalGradient.transparentToBlack)
)

@Preview
@Composable
private fun DetailScreenPreview() {
    DetailScreenContent(
        DetailUIState(
            MediaTitle("Title"),
            MediaUrl(""),
            MediaExplanation(stringResource(R.string.lorem_ipsum)),
        )
    )
}


