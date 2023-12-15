package caios.android.fanbox.core.logs.puree

import com.cookpad.puree.kotlin.PureeLog
import com.cookpad.puree.kotlin.serializer.PureeLogSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.json.JSONObject

class PureeKotlinSerializer(
    private val formatter: Json,
) : PureeLogSerializer {
    override fun serialize(log: PureeLog): JSONObject {
        return JSONObject(formatter.encodeToString(log as PixiViewActivityLog))
    }
}
