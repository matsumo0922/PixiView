package caios.android.fanbox.di

import caios.android.fanbox.BuildConfig
import caios.android.fanbox.core.common.PixiViewConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePixiViewConfig(): PixiViewConfig {
        return PixiViewConfig(
            applicationId = BuildConfig.APPLICATION_ID,
            buildType = BuildConfig.BUILD_TYPE,
            versionCode = BuildConfig.VERSION_CODE,
            versionName = BuildConfig.VERSION_NAME,
            isDebug = BuildConfig.DEBUG,
            developerPassword = BuildConfig.DEVELOPER_PASSWORD,
            pixivClientId = BuildConfig.PIXIV_CLIENT_ID,
            pixivClientSecret = BuildConfig.PIXIV_CLIENT_SECRET,
            adMobAppId = BuildConfig.ADMOB_APP_ID,
            adMobBannerAdUnitId = BuildConfig.ADMOB_BANNER_AD_UNIT_ID,
            adMobNativeAdUnitId = BuildConfig.ADMOB_NATIVE_AD_UNIT_ID,
        )
    }
}
