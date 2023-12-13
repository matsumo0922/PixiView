package caios.android.fanbox.core.ui.ads

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidViewBinding
import caios.android.fanbox.core.common.PixiViewConfig
import caios.android.fanbox.core.ui.databinding.LayoutNativeAdsSmallBinding
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions

@SuppressLint("MissingPermission")
@Composable
fun NativeAdSmallItem(
    nativeAdUnitId: String,
    nativeAdsPreLoader: NativeAdsPreLoader,
    modifier: Modifier = Modifier,
    colorSet: NativeAdColorSet? = null
) {
    AndroidViewBinding(
        modifier = modifier,
        factory = { inflater, parent, attachToParent ->
            val binding = LayoutNativeAdsSmallBinding.inflate(inflater, parent, attachToParent)

            colorSet?.let {
                binding.tvBody.setTextColor(it.text.toArgb())
                binding.btnCta.setTextColor(it.buttonText.toArgb())
                binding.btnCta.backgroundTintList = ColorStateList.valueOf(it.buttonBackground.toArgb())
                binding.tvHeadline.setTextColor(it.text.toArgb())
                binding.tvStore.setTextColor(it.text.toArgb())
            }

            binding
        },
        update = {
            val adView = root.also { adView ->
                adView.bodyView = tvBody
                adView.callToActionView = btnCta
                adView.headlineView = tvHeadline
                adView.iconView = ivAppIcon
                adView.storeView = tvStore
            }

            fun setupNativeAd(nativeAd: NativeAd) {
                nativeAd.body?.let { tvBody.text = it }
                nativeAd.callToAction?.let { btnCta.text = it }
                nativeAd.headline?.let { tvHeadline.text = it }
                nativeAd.icon?.let { ivAppIcon.setImageDrawable(it.drawable) }
                nativeAd.store?.let { tvStore.text = it }
                adView.setNativeAd(nativeAd)
            }

            val preloadedNativeAd = nativeAdsPreLoader.popAd()

            if (preloadedNativeAd != null) {
                setupNativeAd(preloadedNativeAd)
            } else {
                val adLoader = AdLoader.Builder(adView.context, nativeAdUnitId)
                    .forNativeAd { nativeAd ->
                        setupNativeAd(nativeAd)
                    }
                    .withNativeAdOptions(NativeAdOptions.Builder().build())
                    .build()
                adLoader.loadAd(AdRequest.Builder().build())
            }
        }
    )
}

data class NativeAdColorSet(
    val text: Color,
    val buttonText: Color,
    val buttonBackground: Color,
    val adText: Color,
)

@Preview(showBackground = true)
@Composable
private fun NativeSmallAdPreview() {
    NativeAdSmallItem(
        nativeAdUnitId = "",
        nativeAdsPreLoader = NativeAdsPreLoader(LocalContext.current, PixiViewConfig.dummy()),
    )
}
