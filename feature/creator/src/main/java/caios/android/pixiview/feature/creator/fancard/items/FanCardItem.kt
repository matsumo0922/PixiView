package caios.android.pixiview.feature.creator.fancard.items

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import caios.android.pixiview.core.model.fanbox.FanboxCreatorPlanDetail
import caios.android.pixiview.core.ui.extensition.FadePlaceHolder
import caios.android.pixiview.core.ui.extensition.fanboxHeader
import caios.android.pixiview.feature.creator.R
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import java.time.format.DateTimeFormatter

@Composable
internal fun FanCardItem(
    planDetail: FanboxCreatorPlanDetail,
    isDisplayName: Boolean,
    modifier: Modifier = Modifier,
) {
    var isLight by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .aspectRatio(16 / 9f)
            .clip(RoundedCornerShape(4.dp)),
    ) {
        SubcomposeAsyncImage(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            model = ImageRequest.Builder(LocalContext.current)
                .fanboxHeader()
                .allowHardware(false)
                .data(planDetail.supporterCardImageUrl)
                .listener(
                    onSuccess = { _, result ->
                        isLight = isBitmapLightOrDark(result.drawable.toBitmap())
                    },
                )
                .build(),
            loading = {
                FadePlaceHolder()
            },
            contentScale = ContentScale.Crop,
            contentDescription = null,
        )

        TitleItem(
            modifier = Modifier
                .padding(24.dp, 16.dp)
                .align(Alignment.TopEnd),
            planDetail = planDetail,
        )

        NameItem(
            modifier = Modifier
                .padding(24.dp, 16.dp)
                .align(Alignment.BottomStart),
            planDetail = planDetail,
            isDisplayName = isDisplayName,
        )

        Image(
            modifier = Modifier
                .padding(24.dp, 16.dp)
                .align(Alignment.BottomEnd),
            painter = painterResource(if (isLight) R.drawable.im_fanbox_logo_light else R.drawable.im_fanbox_logo_dark),
            contentDescription = null,
        )
    }
}

@Composable
private fun NameItem(
    planDetail: FanboxCreatorPlanDetail,
    isDisplayName: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = "NAME",
                style = cardTextStyle(DEFAULT_TEXT_SIZE),
            )

            Text(
                text = if (isDisplayName) planDetail.supportTransactions.first().user.name else "* * * *",
                style = cardTextStyle(LARGE_TEXT_SIZE),
            )
        }

        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = "SINCE",
                style = cardTextStyle(DEFAULT_TEXT_SIZE),
            )

            Text(
                text = planDetail.supportTransactions.last().transactionDatetime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")),
                style = cardTextStyle(LARGE_TEXT_SIZE),
            )
        }
    }
}

@Composable
private fun TitleItem(
    planDetail: FanboxCreatorPlanDetail,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = planDetail.plan.title,
            style = cardTextStyle(16.sp),
        )

        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = planDetail.plan.fee.toString(),
                style = cardTextStyle(LARGE_TEXT_SIZE),
            )

            Text(
                text = "JPY / month",
                style = cardTextStyle(DEFAULT_TEXT_SIZE),
            )
        }
    }
}

private val DEFAULT_TEXT_SIZE = 10.sp
private val LARGE_TEXT_SIZE = 14.sp

private fun cardTextStyle(fontSize: TextUnit) = TextStyle(
    fontSize = fontSize,
    fontFamily = FontFamily.SansSerif,
    color = Color.White,
    shadow = Shadow(
        color = Color.Black,
        offset = Offset(3f, 3f),
        blurRadius = 3f,
    ),
)

private fun isBitmapLightOrDark(bitmap: Bitmap): Boolean {
    val palette = Palette.from(bitmap).generate()
    val dominantColor = palette.getDominantColor(0)
    return isColorLight(dominantColor)
}

private fun isColorLight(color: Int): Boolean {
    val red = (color shr 16) and 0xff
    val green = (color shr 8) and 0xff
    val blue = color and 0xff

    // 輝度の計算
    val brightness = (red * 0.299) + (green * 0.587) + (blue * 0.114)
    return brightness > 160 // 160を閾値として明るさを判断
}

@Preview
@Composable
private fun FanCardItemPreview() {
    FanCardItem(
        modifier = Modifier.fillMaxWidth(),
        planDetail = FanboxCreatorPlanDetail.dummy(),
        isDisplayName = true,
    )
}
