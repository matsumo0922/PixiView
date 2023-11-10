package caios.android.pixiview.feature.creator.items

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import caios.android.pixiview.core.model.fanbox.FanboxCreatorDetail
import caios.android.pixiview.core.ui.extensition.SimmerPlaceHolder
import caios.android.pixiview.core.ui.extensition.fanboxHeader
import caios.android.pixiview.core.ui.extensition.marquee
import caios.android.pixiview.core.ui.theme.bold
import caios.android.pixiview.core.ui.theme.center
import caios.android.pixiview.feature.creator.R
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreatorTopHeader(
    creatorDetail: FanboxCreatorDetail,
    onClickTerminate: () -> Unit,
    onClickMenu: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        SubcomposeAsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4 / 3f)
                .blur(8.dp),
            model = ImageRequest.Builder(LocalContext.current)
                .fanboxHeader()
                .crossfade(true)
                .data(creatorDetail.coverImageUrl ?: creatorDetail.user.iconUrl)
                .build(),
            loading = {
                SimmerPlaceHolder()
            },
            contentScale = ContentScale.Crop,
            contentDescription = null,
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4 / 3f)
                .align(Alignment.TopCenter)
                .background(Brush.verticalGradient(listOf(Color.Transparent, MaterialTheme.colorScheme.surface))),
        )

        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .size(200.dp)
                .aspectRatio(1f),
            shape = RoundedCornerShape(8.dp),
        ) {
            SubcomposeAsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                model = ImageRequest.Builder(LocalContext.current)
                    .fanboxHeader()
                    .crossfade(true)
                    .error(R.drawable.im_default_user)
                    .data(creatorDetail.user.iconUrl)
                    .build(),
                loading = {
                    SimmerPlaceHolder()
                },
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )
        }

        Text(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .marquee(),
            text = creatorDetail.user.name,
            style = MaterialTheme.typography.headlineSmall.center().bold(),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        TopAppBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding(),
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent,
            ),
            windowInsets = WindowInsets(0, 0, 0, 0),
            title = { /* do nothing */ },
            navigationIcon = {
                Icon(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(50))
                        .clickable { onClickTerminate.invoke() }
                        .padding(6.dp),
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                )
            },
            actions = {
                Icon(
                    modifier = Modifier
                        .size(40.dp)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(50))
                        .clickable { onClickMenu.invoke() }
                        .padding(6.dp),
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                )
            },
        )
    }
}
