package caios.android.pixiview.core.model.fanbox

import caios.android.pixiview.core.model.fanbox.id.CreatorId

data class FanboxCreatorDetail(
    val creatorId: CreatorId,
    val coverImageUrl: String?,
    val description: String,
    val hasAdultContent: Boolean,
    val hasBoothShop: Boolean,
    val isAcceptingRequest: Boolean,
    val isFollowed: Boolean,
    val isStopped: Boolean,
    val isSupported: Boolean,
    val profileItems: List<ProfileItem>,
    val profileLinks: List<String>,
    val user: FanboxUser,
) {
    data class ProfileItem(
        val id: String,
        val imageUrl: String,
        val thumbnailUrl: String,
        val type: String,
    )
}
