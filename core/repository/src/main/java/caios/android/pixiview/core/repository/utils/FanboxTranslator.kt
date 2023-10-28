package caios.android.pixiview.core.repository.utils

import androidx.core.net.toUri
import caios.android.pixiview.core.model.PageInfo
import caios.android.pixiview.core.model.fanbox.FanboxCover
import caios.android.pixiview.core.model.fanbox.FanboxCreator
import caios.android.pixiview.core.model.fanbox.FanboxCreatorTag
import caios.android.pixiview.core.model.fanbox.FanboxCursor
import caios.android.pixiview.core.model.fanbox.FanboxPost
import caios.android.pixiview.core.model.fanbox.FanboxUser
import caios.android.pixiview.core.model.fanbox.entity.FanboxCreatorEntity
import caios.android.pixiview.core.model.fanbox.entity.FanboxCreatorItemsEntity
import caios.android.pixiview.core.model.fanbox.entity.FanboxCreatorTagsEntity
import caios.android.pixiview.core.model.fanbox.entity.FanboxPostItemsEntity

internal fun FanboxPostItemsEntity.translate(): PageInfo<FanboxPost> {
    return PageInfo(
        contents = body.items.map {
            FanboxPost(
                id = it.id,
                title = it.title,
                excerpt = it.excerpt,
                publishedDatetime = it.publishedDatetime,
                updatedDatetime = it.updatedDatetime,
                isLiked = it.isLiked,
                likeCount = it.likeCount,
                commentCount = it.commentCount,
                feeRequired = it.feeRequired,
                isRestricted = it.isRestricted,
                hasAdultContent = it.hasAdultContent,
                tags = it.tags,
                cover = it.cover?.let { cover ->
                    FanboxCover(
                        type = cover.type,
                        url = cover.url,
                    )
                },
                user = FanboxUser(
                    userId = it.user.userId,
                    creatorId = it.creatorId,
                    name = it.user.name,
                    iconUrl = it.user.iconUrl,
                ),
            )
        },
        cursor = body.nextUrl?.translateToCursor(),
    )
}

internal fun FanboxCreatorItemsEntity.translate(): List<FanboxCreator> {
    return body.map {
        FanboxCreator(
            creatorId = it.creatorId,
            coverImageUrl = it.coverImageUrl,
            description = it.description,
            hasAdultContent = it.hasAdultContent,
            hasBoothShop = it.hasBoothShop,
            isAcceptingRequest = it.isAcceptingRequest,
            isFollowed = it.isFollowed,
            isStopped = it.isStopped,
            isSupported = it.isSupported,
            profileItems = it.profileItems.map { profileItem ->
                FanboxCreator.ProfileItem(
                    id = profileItem.id,
                    imageUrl = profileItem.imageUrl,
                    thumbnailUrl = profileItem.thumbnailUrl,
                    type = profileItem.type,
                )
            },
            profileLinks = it.profileLinks,
            user = FanboxUser(
                userId = it.user.userId,
                creatorId = it.creatorId,
                name = it.user.name,
                iconUrl = it.user.iconUrl,
            ),
        )
    }
}

internal fun FanboxCreatorEntity.translate(): FanboxCreator {
    return FanboxCreator(
        creatorId = body.creatorId,
        coverImageUrl = body.coverImageUrl,
        description = body.description,
        hasAdultContent = body.hasAdultContent,
        hasBoothShop = body.hasBoothShop,
        isAcceptingRequest = body.isAcceptingRequest,
        isFollowed = body.isFollowed,
        isStopped = body.isStopped,
        isSupported = body.isSupported,
        profileItems = body.profileItems.map { profileItem ->
            FanboxCreator.ProfileItem(
                id = profileItem.id,
                imageUrl = profileItem.imageUrl,
                thumbnailUrl = profileItem.thumbnailUrl,
                type = profileItem.type,
            )
        },
        profileLinks = body.profileLinks,
        user = FanboxUser(
            userId = body.user.userId,
            creatorId = body.creatorId,
            name = body.user.name,
            iconUrl = body.user.iconUrl,
        ),
    )
}

internal fun FanboxCreatorTagsEntity.translate(): List<FanboxCreatorTag> {
    return body.map {
        FanboxCreatorTag(
            count = it.count,
            coverImageUrl = it.coverImageUrl,
            tag = it.tag,
        )
    }
}

private fun String.translateToCursor(): FanboxCursor {
    val uri = this.toUri()

    return FanboxCursor(
        maxPublishedDatetime = uri.getQueryParameter("maxPublishedDatetime")!!,
        maxId = uri.getQueryParameter("maxId")!!,
    )
}
