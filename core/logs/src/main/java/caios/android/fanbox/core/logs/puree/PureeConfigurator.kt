package caios.android.fanbox.core.logs.puree

import android.content.Context
import androidx.lifecycle.ProcessLifecycleOwner
import caios.android.fanbox.core.common.PixiViewConfig
import caios.android.fanbox.core.model.UserData
import com.cookpad.puree.kotlin.PureeLogger
import com.cookpad.puree.kotlin.store.DbPureeLogStore
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

object PureeConfigurator {

    @OptIn(ExperimentalSerializationApi::class)
    private val formatter = Json {
        isLenient = true
        prettyPrint = true
        ignoreUnknownKeys = true
        coerceInputValues = true
        encodeDefaults = true
        explicitNulls = false
    }

    fun configure(
        context: Context,
        pixiViewConfig: PixiViewConfig,
        userData: UserData,
    ) {
        val logger = buildPureeLogger(context, pixiViewConfig, userData)

        Puree.setPureeLogger(PureeLoggerWrapper(logger))
        Puree.flush()
    }

    private fun buildPureeLogger(
        context: Context,
        pixiViewConfig: PixiViewConfig,
        userData: UserData,
    ): PureeLogger {
        return PureeLogger.Builder(
            lifecycle = ProcessLifecycleOwner.get().lifecycle,
            logSerializer = PureeKotlinSerializer(formatter),
            logStore = DbPureeLogStore(context, "puree-kotlin.db"),
        )
            .filter(
                PixiViewLogFilter(pixiViewConfig, userData, userAgent = "PixiView"),
                PixiViewActivityLog::class.java,
            )
            .output(
                PixiViewLogOutput(),
                PixiViewActivityLog::class.java,
            )
            .build()
    }
}
