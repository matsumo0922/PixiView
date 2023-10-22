package caios.android.pixiview.di

import android.content.Context
import caios.android.pixiview.BuildConfig
import caios.android.pixiview.core.common.PixiViewConfig
import caios.android.pixiview.core.common.di.ApplicationScope
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
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
        )
    }
}
