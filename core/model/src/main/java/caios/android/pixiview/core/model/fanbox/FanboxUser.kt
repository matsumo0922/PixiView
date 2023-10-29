package caios.android.pixiview.core.model.fanbox

import caios.android.pixiview.core.model.fanbox.id.CreatorId

data class FanboxUser(
    val userId: String,
    val creatorId: CreatorId,
    val name: String,
    val iconUrl: String?,
) {
    companion object {
        fun dummy() = FanboxUser(
            userId = "",
            creatorId = CreatorId(""),
            name = "あいらんど",
            iconUrl = "https://pixiv.pximg.net/c/160x160_90_a2_g5/fanbox/public/images/user/24164271/icon/3sMCyeX4owee4LwXhwpyMkpv.jpeg",
        )
    }
}
