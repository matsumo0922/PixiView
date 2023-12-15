package caios.android.fanbox.core.logs.puree

import com.cookpad.puree.kotlin.output.PureeBufferedOutput
import org.json.JSONObject
import timber.log.Timber
import java.time.Duration

class PixiViewLogOutput : PureeBufferedOutput("pixiview_activity_log") {

    override val flushInterval: Duration = Duration.ofSeconds(15)

    override fun emit(logs: List<JSONObject>, onSuccess: () -> Unit, onFailed: (Throwable) -> Unit) {
        Timber.d("PureeLog: $logs")
        onSuccess.invoke()
    }
}
