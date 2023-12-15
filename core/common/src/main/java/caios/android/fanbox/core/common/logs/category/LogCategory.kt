package caios.android.fanbox.core.common.logs.category

import kotlinx.serialization.json.JsonObject

interface LogCategory {
    val properties: JsonObject
}
