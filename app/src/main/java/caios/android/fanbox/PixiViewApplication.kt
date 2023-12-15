package caios.android.fanbox

import android.app.Application
import android.content.Intent
import android.os.Build
import caios.android.fanbox.core.common.PixiViewConfig
import caios.android.fanbox.core.common.PixiViewDebugTree
import caios.android.fanbox.core.logs.puree.PureeConfigurator
import caios.android.fanbox.core.repository.UserDataRepository
import caios.android.fanbox.feature.report.CrashReportActivity
import coil.Coil
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.google.android.gms.ads.MobileAds
import com.google.android.material.color.DynamicColors
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class PixiViewApplication : Application() {

    @Inject
    lateinit var userDataRepository: UserDataRepository

    @Inject
    lateinit var pixiViewConfig: PixiViewConfig

    override fun onCreate() {
        super.onCreate()

        Timber.plant(PixiViewDebugTree())

        MobileAds.initialize(this)

        DynamicColors.applyToActivitiesIfAvailable(this)

        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            startCrushReportActivity(e)
        }

        setupLogger()
        setupCoil()
        setupFirebase()
    }

    private fun startCrushReportActivity(e: Throwable) {
        Timber.e(e)

        startActivity(
            Intent(this, CrashReportActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra("report", getVersionReport() + "\n" + e.stackTraceToString())
            },
        )
    }

    private fun getVersionReport() = buildString {
        val release = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) Build.VERSION.RELEASE_OR_CODENAME else Build.VERSION.RELEASE

        appendLine("Version: ${pixiViewConfig.versionName} (${pixiViewConfig.versionCode})")
        appendLine("Device Information: $release (${Build.VERSION.SDK_INT})")
        appendLine("Device Model: ${Build.MODEL} (${Build.MANUFACTURER})")
        appendLine("Supported ABIs: ${Build.SUPPORTED_ABIS.contentToString()}")
    }

    private fun setupCoil() {
        val imageLoader = ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(this.cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.02)
                    .build()
            }
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .respectCacheHeaders(false)
            .crossfade(true)
            .build()

        Coil.setImageLoader(imageLoader)
    }

    private fun setupFirebase() {
        val builder = FirebaseOptions.Builder()
            .setProjectId("pixiview-b5dc7")
            .setApplicationId("1:561281916572:android:0952e4cd0fe2fe01ab1759")
            .setApiKey("AIzaSyC6SxUp9ipDpVjQMSHmcgy7SZMnPUwqAmA")
            .setDatabaseUrl("https://pixiview-b5dc7.firebaseio.com")
            .setStorageBucket("pixiview-b5dc7.appspot.com")

        FirebaseApp.initializeApp(this, builder.build())
    }

    private fun setupLogger() {
        MainScope().launch {
            PureeConfigurator.configure(
                context = this@PixiViewApplication,
                pixiViewConfig = pixiViewConfig,
                userData = userDataRepository.userData.first(),
            )
        }
    }
}
