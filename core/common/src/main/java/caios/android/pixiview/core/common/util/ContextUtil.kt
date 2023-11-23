package caios.android.pixiview.core.common.util

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity

object ContextUtil {
    fun Context.getActivity(): ComponentActivity? = when (this) {
        is ComponentActivity -> this
        is ContextWrapper -> baseContext.getActivity()
        else -> null
    }
}
