package caios.android.pixiview.core.repository.utils

import androidx.core.net.toUri
import caios.android.pixiview.core.model.PageInfo
import caios.android.pixiview.core.model.fanbox.FanboxCover
import caios.android.pixiview.core.model.fanbox.FanboxCursor
import caios.android.pixiview.core.model.fanbox.FanboxPost
import caios.android.pixiview.core.model.fanbox.FanboxUser
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
        cursor = body.nextUrl.translateToCursor(),
    )
}

private fun String.translateToCursor(): FanboxCursor {
    val uri = this.toUri()

    return FanboxCursor(
        maxPublishedDatetime = uri.getQueryParameter("maxPublishedDatetime")!!,
        maxId = uri.getQueryParameter("maxId")!!,
    )
}
