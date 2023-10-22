package caios.android.pixiview.core.common.util

import timber.log.Timber
import kotlin.coroutines.cancellation.CancellationException

suspend fun <T> suspendRunCatching(block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (cancellationException: CancellationException) {
    throw cancellationException
} catch (exception: Exception) {
    Timber.i(exception, "Failed to evaluate a suspendRunCatchingBlock. Returning failure Result")
    Result.failure(exception)
}
