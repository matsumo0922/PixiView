package caios.android.fanbox.core.logs.puree

import android.os.Build
import caios.android.fanbox.core.common.PixiViewConfig
import caios.android.fanbox.core.logs.CommonPayload
import caios.android.fanbox.core.model.BuildConfig
import caios.android.fanbox.core.model.UserData
import com.cookpad.puree.kotlin.PureeFilter
import org.json.JSONObject
import java.time.ZoneId

class PixiViewLogFilter(
    private val pixiViewConfig: PixiViewConfig,
    private val userData: UserData,
    private val userAgent: String,
) : PureeFilter {

    override fun applyFilter(log: JSONObject): JSONObject {
        val commonPayload = CommonPayload(
            pixiviewId = userData.pixiViewId,
            userAgent = userAgent,
            isPlus = userData.isPlusMode,
            isDeveloper = userData.isDeveloperMode,
            isTester = userData.isTestUser,
            osVersion = Build.VERSION.SDK_INT.toString(),
            applicationVariant = BuildConfig.BUILD_TYPE,
            applicationVersionCode = pixiViewConfig.versionCode.toLong(),
            applicationVersionName = pixiViewConfig.versionName,
            timeZone = ZoneId.systemDefault().id
        )

        return commonPayload.applyToJsonObject(log)
    }
}
