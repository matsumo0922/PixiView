package caios.android.fanbox.core.common.util

import android.os.Bundle

fun buildBundle(f: Bundle.() -> Unit): Bundle {
    return Bundle().apply(f)
}
