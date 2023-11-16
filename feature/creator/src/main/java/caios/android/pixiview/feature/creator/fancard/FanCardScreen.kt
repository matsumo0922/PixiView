package caios.android.pixiview.feature.creator.fancard

import android.graphics.Bitmap
import android.graphics.Picture
import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import caios.android.pixiview.core.common.util.ToastUtil
import caios.android.pixiview.core.model.ScreenState
import caios.android.pixiview.core.model.fanbox.FanboxCreatorPlanDetail
import caios.android.pixiview.core.model.fanbox.id.CreatorId
import caios.android.pixiview.core.ui.AsyncLoadContents
import caios.android.pixiview.core.ui.component.PixiViewTopBar
import caios.android.pixiview.feature.creator.R
import caios.android.pixiview.feature.creator.fancard.items.FanCardItem
import caios.android.pixiview.feature.creator.fancard.items.FanCardMenuDialog
import kotlinx.coroutines.flow.collectLatest
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@Composable
internal fun FanCardRoute(
    creatorId: CreatorId,
    terminate: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FanCardViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()

    LaunchedEffect(creatorId) {
        if (screenState !is ScreenState.Idle) {
            viewModel.fetch(creatorId)
        }
    }

    LaunchedEffect(true) {
        viewModel.downloadedEvent.collectLatest {
            ToastUtil.show(context, if (it) R.string.common_downloaded else R.string.error_network)
        }
    }

    AsyncLoadContents(
        modifier = modifier,
        screenState = screenState,
        retryAction = { terminate.invoke() },
    ) {
        FanCardScreen(
            modifier = Modifier.fillMaxWidth(),
            planDetail = it.planDetail,
            onClickDownload = viewModel::download,
            onTerminate = terminate,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FanCardScreen(
    planDetail: FanboxCreatorPlanDetail,
    onClickDownload: (Bitmap, FanboxCreatorPlanDetail) -> Unit,
    onTerminate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val zoomState = rememberZoomState()
    val picture = remember { Picture() }

    var isDisplayName by remember { mutableStateOf(true) }
    var isShowMenu by remember { mutableStateOf(false) }

    Box(modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zoomable(zoomState)
                .padding(16.dp),
        ) {
            FanCardItem(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .drawWithCache {
                        // example that shows how to redirect rendering to an android picture and then
                        // draw the picture into the original destination
                        val width = this.size.width.toInt()
                        val height = this.size.height.toInt()

                        onDrawWithContent {
                            val pictureCanvas = Canvas(
                                picture.beginRecording(
                                    width,
                                    height,
                                ),
                            )
                            // requires at least 1.6.0-alpha01+
                            draw(this, this.layoutDirection, pictureCanvas, this.size) {
                                this@onDrawWithContent.drawContent()
                            }
                            picture.endRecording()

                            drawIntoCanvas { canvas -> canvas.nativeCanvas.drawPicture(picture) }
                            drawContent()
                        }
                    },
                planDetail = planDetail,
                isDisplayName = isDisplayName,
            )
        }

        PixiViewTopBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth(),
            onClickActions = { isShowMenu = true },
            onClickNavigation = onTerminate,
            isTransparent = true,
        )

        if (isShowMenu) {
            FanCardMenuDialog(
                onClickSwitchDisplayName = { isDisplayName = !isDisplayName },
                onClickDownload = { onClickDownload.invoke(picture.asBitmap(), planDetail) },
                onDismissRequest = { isShowMenu = false },
            )
        }
    }
}

private fun Picture.asBitmap(): Bitmap {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        Bitmap.createBitmap(this)
    } else {
        val bitmap = Bitmap.createBitmap(
            width,
            height,
            Bitmap.Config.ARGB_8888,
        )
        val canvas = android.graphics.Canvas(bitmap)
        canvas.drawColor(android.graphics.Color.WHITE)
        canvas.drawPicture(this)
        bitmap
    }
}
