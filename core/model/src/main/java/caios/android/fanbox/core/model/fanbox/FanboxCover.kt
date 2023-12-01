package caios.android.fanbox.core.model.fanbox

import kotlinx.serialization.Serializable

@Serializable
data class FanboxCover(
    val url: String,
    val type: String,
)
