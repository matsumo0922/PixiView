package caios.android.fanbox.core.logs.puree

import caios.android.fanbox.core.logs.category.LogCategory
import com.cookpad.puree.kotlin.PureeLog
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal object PixiViewActivityLogger {
    fun post(logCategory: LogCategory) = logCategory.run {
        PixiViewActivityLog(properties).also {
            Puree.send(it)
        }
    }
}

@Serializable
data class PixiViewActivityLog internal constructor(
    private var properties: JsonObject,
) : PureeLog {

    init {
        properties = buildJsonObject {
            for (key in properties.keys) {
                put(key, properties[key]!!)
            }

            put("table_name", tableName)
        }
    }

    companion object {
        private const val tableName: String = "android_pixiview_activity"
    }
}

fun LogCategory.send() = PixiViewActivityLogger.post(this)
