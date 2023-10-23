package caios.android.pixiview.core.model

import caios.android.pixiview.core.model.fanbox.FanboxCursor

data class PageInfo<T>(
    val contents: List<T>,
    val cursor: FanboxCursor?,
)
