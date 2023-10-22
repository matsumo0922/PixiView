package caios.android.pixiview.core.common.util

import android.os.Bundle

fun buildBundle(f: Bundle.() -> Unit): Bundle {
    return Bundle().apply(f)
}
