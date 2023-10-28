package caios.android.pixiview.core.repository.utils

import androidx.core.net.toUri
import caios.android.pixiview.core.model.PageInfo
import caios.android.pixiview.core.model.fanbox.FanboxCover
import caios.android.pixiview.core.model.fanbox.FanboxCreator
import caios.android.pixiview.core.model.fanbox.FanboxCreatorPlan
import caios.android.pixiview.core.model.fanbox.FanboxCreatorTag
import caios.android.pixiview.core.model.fanbox.FanboxCursor
import caios.android.pixiview.core.model.fanbox.FanboxPost
import caios.android.pixiview.core.model.fanbox.FanboxPostDetail
import caios.android.pixiview.core.model.fanbox.FanboxUser
import caios.android.pixiview.core.model.fanbox.entity.FanboxCreatorEntity
import caios.android.pixiview.core.model.fanbox.entity.FanboxCreatorItemsEntity
import caios.android.pixiview.core.model.fanbox.entity.FanboxCreatorPlansEntity
import caios.android.pixiview.core.model.fanbox.entity.FanboxCreatorTagsEntity
import caios.android.pixiview.core.model.fanbox.entity.FanboxPostDetailEntity
import caios.android.pixiview.core.model.fanbox.entity.FanboxPostItemsEntity
import timber.log.Timber

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

internal fun FanboxPostDetailEntity.translate(): FanboxPostDetail {
    var bodyBlock: FanboxPostDetail.Body? = null

    if (!body.body?.blocks.isNullOrEmpty()) {
        body.body?.blocks?.let { blocks ->
            // 文字列や画像、ファイルなどのブロックが混在している場合

            val images = body.body?.imageMap.orEmpty()
            val files = body.body?.fileMap.orEmpty()

            bodyBlock = FanboxPostDetail.Body.Article(
                blocks = blocks.mapNotNull {
                    when {
                        it.text != null -> {
                            FanboxPostDetail.Body.Article.Block.Text(it.text!!)
                        }
                        it.imageId != null -> {
                            images[it.imageId!!]?.let { image ->
                                FanboxPostDetail.Body.Article.Block.Image(
                                    extension = image.extension,
                                    originalUrl = image.originalUrl,
                                    thumbnailUrl = image.thumbnailUrl,
                                )
                            }
                        }
                        it.fileId != null -> {
                            files[it.fileId!!]?.let { file ->
                                FanboxPostDetail.Body.Article.Block.File(
                                    extension = file.extension,
                                    name = file.name,
                                    size = file.size,
                                    url = file.url,
                                )
                            }
                        }
                        else -> {
                            Timber.d("FanboxPostDetailEntity translate error: Unknown block type. $it")
                            null
                        }
                    }
                }
            )
        }
    }

    if (!body.body?.images.isNullOrEmpty()) {
        body.body?.images?.let { blocks ->
            // 画像のみのブロックの場合

            bodyBlock = FanboxPostDetail.Body.Image(
                text = body.body?.text.orEmpty(),
                images = blocks.map {
                    FanboxPostDetail.Body.Image.ImageItem(
                        id = it.id,
                        extension = it.extension,
                        originalUrl = it.originalUrl,
                        thumbnailUrl = it.thumbnailUrl,
                    )
                }
            )
        }
    }

    if (!body.body?.files.isNullOrEmpty()) {
        body.body?.files?.let { blocks ->
            // ファイルのみのブロックの場合

            bodyBlock = FanboxPostDetail.Body.File(
                text = body.body?.text.orEmpty(),
                files = blocks.map {
                    FanboxPostDetail.Body.File.FileItem(
                        id = it.id,
                        name = it.name,
                        extension = it.extension,
                        size = it.size,
                        url = it.url,
                    )
                }
            )
        }
    }

    return FanboxPostDetail(
        id = body.id,
        title = body.title,
        creatorId = body.creatorId,
        publishedDatetime = body.publishedDatetime,
        updatedDatetime = body.updatedDatetime,
        isLiked = body.isLiked,
        likeCount = body.likeCount,
        commentCount = body.commentCount,
        feeRequired = body.feeRequired,
        isRestricted = body.isRestricted,
        hasAdultContent = body.hasAdultContent,
        tags = body.tags,
        user = FanboxUser(
            userId = body.user.userId,
            creatorId = body.creatorId,
            name = body.user.name,
            iconUrl = body.user.iconUrl,
        ),
        body = bodyBlock!!,
        excerpt = body.excerpt,
        commentList = body.commentList.let { commentList ->
            FanboxPostDetail.Comment(
                items = commentList.items.map {
                    FanboxPostDetail.Comment.CommentItem(
                        body = it.body,
                        createdDatetime = it.createdDatetime,
                        id = it.id,
                        isLiked = it.isLiked,
                        isOwn = it.isOwn,
                        likeCount = it.likeCount,
                        parentCommentId = it.parentCommentId,
                        rootCommentId = it.rootCommentId,
                        user = FanboxUser(
                            userId = it.user.userId,
                            creatorId = "",
                            name = it.user.name,
                            iconUrl = it.user.iconUrl,
                        ),
                    )
                },
                cursor = commentList.nextUrl?.translateToCursor(),
            )
        },
        nextPost = body.nextPost?.let {
            FanboxPostDetail.OtherPost(
                id = it.id,
                title = it.title,
                publishedDatetime = it.publishedDatetime,
            )
        },
        prevPost = body.prevPost?.let {
            FanboxPostDetail.OtherPost(
                id = it.id,
                title = it.title,
                publishedDatetime = it.publishedDatetime,
            )
        },
        imageForShare = body.imageForShare,
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

internal fun FanboxCreatorPlansEntity.translate(): List<FanboxCreatorPlan> {
    return body.map {
        FanboxCreatorPlan(
            coverImageUrl = it.coverImageUrl,
            description = it.description,
            fee = it.fee,
            hasAdultContent = it.hasAdultContent,
            id = it.id,
            paymentMethod = it.paymentMethod,
            title = it.title,
            user = FanboxUser(
                userId = it.user.userId,
                creatorId = it.creatorId,
                name = it.user.name,
                iconUrl = it.user.iconUrl,
            ),
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
