package caios.android.fanbox.core.logs.puree

import com.cookpad.puree.kotlin.PureeLog
import com.cookpad.puree.kotlin.PureeLogger

object Puree {
    private var pureeLoggerInstance: LoggerWrapper? = null
    private val pureeLogger: LoggerWrapper
        get() = checkNotNull(pureeLoggerInstance) { "PureeLogger instance is not set" }

    fun setPureeLogger(pureeLogger: LoggerWrapper) {
        pureeLoggerInstance = pureeLogger
    }

    @JvmStatic
    fun send(log: PureeLog) {
        pureeLogger.postLog(log)
    }

    @JvmStatic
    fun flush() {
        pureeLogger.flush()
    }

    interface LoggerWrapper {
        fun postLog(log: PureeLog)
        fun flush()
    }
}

class PureeLoggerWrapper(private val logger: PureeLogger) : Puree.LoggerWrapper {
    override fun postLog(log: PureeLog) {
        logger.postLog(log)
    }

    override fun flush() {
        logger.flush()
    }
}
