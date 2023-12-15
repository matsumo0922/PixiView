package caios.android.fanbox.core.datastore

import android.content.Context
import caios.android.fanbox.core.common.serializer.OffsetDateTimeSerializer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.io.File
import java.time.OffsetDateTime
import javax.inject.Inject

class PreferenceLaunchLog @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val logFile get() = File(context.filesDir, FILE_NAME)

    fun save(time: OffsetDateTime) {
        val logs = get().toMutableList()
        val serializer = ListSerializer(OffsetDateTimeSerializer())

        Json.encodeToString(serializer, logs.apply { add(time) }).also {
            logFile.writeText(it)
        }
    }

    fun get(): List<OffsetDateTime> {
        if (!logFile.exists()) return emptyList()

        return logFile.readText().let {
            Json.decodeFromString(ListSerializer(OffsetDateTimeSerializer()), it)
        }
    }

    companion object {
        private const val FILE_NAME = "LaunchLog.json"
    }
}
