package caios.android.pixiview.core.model

import caios.android.pixiview.core.model.fanbox.FanboxCursor

data class PageCursorInfo<T>(
    val contents: List<T>,
    val cursor: FanboxCursor?,
)

data class PageNumberInfo<T>(
    val contents: List<T>,
    val nextPage: Int?,
)
