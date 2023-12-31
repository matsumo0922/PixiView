package caios.android.fanbox.core.common

import android.util.Log
import timber.log.Timber
import java.util.Locale

class PixiViewDebugTree : Timber.DebugTree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        Log.println(priority, "PixiViewLog", "$message ${getCallerInfo()}")
    }

    @Suppress("ThrowingExceptionsWithoutMessageOrCause")
    private fun getCallerInfo(): String {
        val stackTrace = Throwable().stackTrace

        if (stackTrace.size < 6) {
            // stack[0] PixiViewDebugTree.getCallerInfo()
            // stack[1] PixiViewDebugTree.log()
            // stack[2] Tree.prepareLog()
            // stack[3] Tree.d()
            // stack[4] Forrest.d()
            // stack[5] Timber.d()

            return ""
        }

        val element = stackTrace[5]

        return String.format(Locale.getDefault(), "%s(%s:%s)", element.methodName, element.fileName, element.lineNumber)
    }
}
