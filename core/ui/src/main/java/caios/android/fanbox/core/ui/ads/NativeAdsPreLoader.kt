package caios.android.fanbox.core.ui.ads

import android.annotation.SuppressLint
import android.content.Context
import caios.android.fanbox.core.common.PixiViewConfig
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDateTime
import javax.inject.Inject

data class PreLoadedNativeAd(
    val ad: NativeAd,
    val date: LocalDateTime,
)

class NativeAdsPreLoader @Inject constructor(
    @ApplicationContext context: Context,
    pixiViewConfig: PixiViewConfig,
) {
    private var preloadedNativeAds: MutableList<PreLoadedNativeAd> = mutableListOf()
    private var adLoader: AdLoader

    init {
        adLoader = AdLoader.Builder(context, pixiViewConfig.adMobNativeAdUnitId)
                .forNativeAd { nativeAd ->
                    preloadedNativeAds.add(PreLoadedNativeAd(nativeAd, LocalDateTime.now()))
                }
                .withNativeAdOptions(NativeAdOptions.Builder().build())
                .build()
    }

    @SuppressLint("MissingPermission")
    fun preloadAd() {
        if (adLoader.isLoading) return

        removeExpiredAds()

        val numberOfAds = NUMBER_OF_PRELOAD_ADS - preloadedNativeAds.count()

        if (numberOfAds > 0) {
            adLoader.loadAds(AdRequest.Builder().build(), numberOfAds)
        }
    }

    fun popAd(): NativeAd? {
        removeExpiredAds()

        val first = preloadedNativeAds.firstOrNull()

        return if (first != null) {
            preloadedNativeAds.removeFirst()
            // ここでプリロードしてしまうと、フリークエンシーキャップを設定している場合の効果がなくなる.
            // 従って、広告を表示する少し前にプリロードするのが望ましい
            // preloadAd()
            first.ad
        } else {
            preloadAd()
            null
        }
    }

    /** 期限切れのNativeAdを削除する */
    private fun removeExpiredAds() {
        val adLimitDate = LocalDateTime.now().minusHours(1)
        preloadedNativeAds = preloadedNativeAds.filter { it.date > adLimitDate }.toMutableList()
    }

    companion object {
        const val NUMBER_OF_PRELOAD_ADS = 3
    }
}
