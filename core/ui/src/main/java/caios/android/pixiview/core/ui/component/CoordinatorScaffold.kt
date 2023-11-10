package caios.android.pixiview.core.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import caios.android.pixiview.core.ui.theme.applyTonalElevation
import kotlinx.serialization.json.JsonNull.content

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CoordinatorScaffold(
    header: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    color: Color = MaterialTheme.colorScheme.surface,
    onClickNavigateUp: () -> Unit = {},
    onClickMenu: () -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(0.dp),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: LazyListScope.() -> Unit,
) {
    var appBarAlpha by remember { mutableFloatStateOf(0f) }
    var topSectionHeight by remember { mutableIntStateOf(100) }

    Box(modifier.background(color)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding,
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
            state = listState,
        ) {
            item {
                header.invoke(
                    Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { topSectionHeight = it.size.height },
                )
            }

            content(this)
        }

        CoordinatorToolBar(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.applyTonalElevation(
                backgroundColor = MaterialTheme.colorScheme.surface,
                elevation = 3.dp,
            ),
            backgroundAlpha = appBarAlpha,
            onClickNavigateUp = onClickNavigateUp,
            onClickMenu = onClickMenu,
        )
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }.collect {
            val index = listState.firstVisibleItemIndex
            val disableArea = topSectionHeight * 0.7f
            val alpha = if (index == 0) (listState.firstVisibleItemScrollOffset.toDouble() - disableArea) / (topSectionHeight - disableArea) else 1

            appBarAlpha = (alpha.toFloat() * 2).coerceIn(0f..1f)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CoordinatorToolBar(
    color: Color,
    backgroundAlpha: Float,
    onClickNavigateUp: () -> Unit,
    onClickMenu: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = color.copy(backgroundAlpha),
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = if (backgroundAlpha > 0.9f) 4.dp else 0.dp,
    ) {
        TopAppBar(
            modifier = Modifier.statusBarsPadding(),
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
                        .clickable { onClickNavigateUp.invoke() }
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

@Preview
@Composable
private fun CoordinatorScaffoldPreview() {
    CoordinatorScaffold(
        modifier = Modifier.fillMaxSize(),
        header = {
            Box(
                modifier = it
                    .fillMaxWidth()
                    .height(512.dp)
                    .background(Color.Green),
            )
        },
        content = {
            items(listOf(Color.Blue, Color.Red, Color.Yellow, Color.Cyan)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(256.dp)
                        .background(it),
                )
            }
        },
    )
}
