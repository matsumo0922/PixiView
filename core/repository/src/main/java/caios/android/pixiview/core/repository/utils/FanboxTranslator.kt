package caios.android.pixiview.core.repository.utils

import androidx.core.net.toUri
import caios.android.pixiview.core.model.PageInfo
import caios.android.pixiview.core.model.fanbox.FanboxCover
import caios.android.pixiview.core.model.fanbox.FanboxCreator
import caios.android.pixiview.core.model.fanbox.FanboxCreatorDetail
import caios.android.pixiview.core.model.fanbox.FanboxCreatorPlan
import caios.android.pixiview.core.model.fanbox.FanboxCreatorPlanDetail
import caios.android.pixiview.core.model.fanbox.FanboxCreatorTag
import caios.android.pixiview.core.model.fanbox.FanboxCursor
import caios.android.pixiview.core.model.fanbox.FanboxNewsLetter
import caios.android.pixiview.core.model.fanbox.FanboxPaidRecord
import caios.android.pixiview.core.model.fanbox.FanboxPost
import caios.android.pixiview.core.model.fanbox.FanboxPostDetail
import caios.android.pixiview.core.model.fanbox.FanboxUser
import caios.android.pixiview.core.model.fanbox.entity.FanboxCreatorEntity
import caios.android.pixiview.core.model.fanbox.entity.FanboxCreatorItemsEntity
import caios.android.pixiview.core.model.fanbox.entity.FanboxCreatorPlanEntity
import caios.android.pixiview.core.model.fanbox.entity.FanboxCreatorPlansEntity
import caios.android.pixiview.core.model.fanbox.entity.FanboxCreatorTagsEntity
import caios.android.pixiview.core.model.fanbox.entity.FanboxNewsLattersEntity
import caios.android.pixiview.core.model.fanbox.entity.FanboxPaidRecordEntity
import caios.android.pixiview.core.model.fanbox.entity.FanboxPostDetailEntity
import caios.android.pixiview.core.model.fanbox.entity.FanboxPostItemsEntity
import caios.android.pixiview.core.model.fanbox.id.CreatorId
import caios.android.pixiview.core.model.fanbox.id.NewsLetterId
import caios.android.pixiview.core.model.fanbox.id.PlanId
import caios.android.pixiview.core.model.fanbox.id.PostId
import timber.log.Timber
import java.time.OffsetDateTime

internal fun FanboxPostItemsEntity.translate(): PageInfo<FanboxPost> {
    return PageInfo(
        contents = body.items.map {
            FanboxPost(
                id = PostId(it.id),
                title = it.title,
                excerpt = it.excerpt,
                publishedDatetime = OffsetDateTime.parse(it.publishedDatetime),
                updatedDatetime = OffsetDateTime.parse(it.updatedDatetime),
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
                    creatorId = CreatorId(it.creatorId),
                    name = it.user.name,
                    iconUrl = it.user.iconUrl,
                ),
            )
        },
        cursor = body.nextUrl?.translateToCursor(),
    )
}

internal fun FanboxCreatorItemsEntity.translate(): List<FanboxCreatorDetail> {
    return body.map {
        FanboxCreatorDetail(
            creatorId = CreatorId(it.creatorId),
            coverImageUrl = it.coverImageUrl,
            description = it.description,
            hasAdultContent = it.hasAdultContent,
            hasBoothShop = it.hasBoothShop,
            isAcceptingRequest = it.isAcceptingRequest,
            isFollowed = it.isFollowed,
            isStopped = it.isStopped,
            isSupported = it.isSupported,
            profileItems = it.profileItems.map { profileItem ->
                FanboxCreatorDetail.ProfileItem(
                    id = profileItem.id,
                    imageUrl = profileItem.imageUrl,
                    thumbnailUrl = profileItem.thumbnailUrl,
                    type = profileItem.type,
                )
            },
            profileLinks = it.profileLinks,
            user = FanboxUser(
                userId = it.user.userId,
                creatorId = CreatorId(it.creatorId),
                name = it.user.name,
                iconUrl = it.user.iconUrl,
            ),
        )
    }
}

internal fun FanboxCreatorEntity.translate(): FanboxCreatorDetail {
    return FanboxCreatorDetail(
        creatorId = CreatorId(body.creatorId),
        coverImageUrl = body.coverImageUrl,
        description = body.description,
        hasAdultContent = body.hasAdultContent,
        hasBoothShop = body.hasBoothShop,
        isAcceptingRequest = body.isAcceptingRequest,
        isFollowed = body.isFollowed,
        isStopped = body.isStopped,
        isSupported = body.isSupported,
        profileItems = body.profileItems.map { profileItem ->
            FanboxCreatorDetail.ProfileItem(
                id = profileItem.id,
                imageUrl = profileItem.imageUrl,
                thumbnailUrl = profileItem.thumbnailUrl,
                type = profileItem.type,
            )
        },
        profileLinks = body.profileLinks,
        user = FanboxUser(
            userId = body.user.userId,
            creatorId = CreatorId(body.creatorId),
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
            val urls = body.body?.urlEmbedMap.orEmpty()

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
                        it.urlEmbedId != null -> {
                            urls[it.urlEmbedId!!]?.let { url ->
                                FanboxPostDetail.Body.Article.Block.Link(
                                    html = url.html,
                                )
                            }
                        }
                        else -> {
                            Timber.d("FanboxPostDetailEntity translate error: Unknown block type. $it")
                            null
                        }
                    }
                },
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
                },
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
                },
            )
        }
    }

    return FanboxPostDetail(
        id = PostId(body.id),
        title = body.title,
        publishedDatetime = OffsetDateTime.parse(body.publishedDatetime),
        updatedDatetime = OffsetDateTime.parse(body.updatedDatetime),
        isLiked = body.isLiked,
        likeCount = body.likeCount,
        commentCount = body.commentCount,
        feeRequired = body.feeRequired,
        isRestricted = body.isRestricted,
        hasAdultContent = body.hasAdultContent,
        tags = body.tags,
        user = FanboxUser(
            userId = body.user.userId,
            creatorId = CreatorId(body.creatorId),
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
                        createdDatetime = OffsetDateTime.parse(it.createdDatetime),
                        id = it.id,
                        isLiked = it.isLiked,
                        isOwn = it.isOwn,
                        likeCount = it.likeCount,
                        parentCommentId = it.parentCommentId,
                        rootCommentId = it.rootCommentId,
                        user = FanboxUser(
                            userId = it.user.userId,
                            creatorId = CreatorId(""),
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
                publishedDatetime = OffsetDateTime.parse(it.publishedDatetime),
            )
        },
        prevPost = body.prevPost?.let {
            FanboxPostDetail.OtherPost(
                id = it.id,
                title = it.title,
                publishedDatetime = OffsetDateTime.parse(it.publishedDatetime),
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
            id = PlanId(it.id),
            paymentMethod = it.paymentMethod,
            title = it.title,
            user = FanboxUser(
                userId = it.user.userId,
                creatorId = CreatorId(it.creatorId),
                name = it.user.name,
                iconUrl = it.user.iconUrl,
            ),
        )
    }
}

internal fun FanboxCreatorPlanEntity.translate(): FanboxCreatorPlanDetail {
    return FanboxCreatorPlanDetail(
        plan = FanboxCreatorPlan(
            id = PlanId(body.plan.id),
            title = body.plan.title,
            description = body.plan.description,
            fee = body.plan.fee,
            coverImageUrl = body.plan.coverImageUrl,
            hasAdultContent = body.plan.hasAdultContent,
            paymentMethod = body.plan.paymentMethod,
            user = FanboxUser(
                userId = body.plan.user.userId,
                creatorId = CreatorId(body.plan.creatorId),
                name = body.plan.user.name,
                iconUrl = body.plan.user.iconUrl,
            ),
        ),
        supportStartDatetime = body.supportStartDatetime,
        supportTransactions = body.supportTransactions.map {
            FanboxCreatorPlanDetail.SupportTransaction(
                id = it.id,
                paidAmount = it.paidAmount,
                transactionDatetime = it.transactionDatetime,
                targetMonth = it.targetMonth,
                user = FanboxUser(
                    userId = it.supporter.userId,
                    creatorId = CreatorId(body.plan.creatorId),
                    name = it.supporter.name,
                    iconUrl = it.supporter.iconUrl,
                ),
            )
        },
        supporterCardImageUrl = body.supporterCardImageUrl,
    )
}

internal fun FanboxPaidRecordEntity.translate(): List<FanboxPaidRecord> {
    return body.map {
        FanboxPaidRecord(
            id = it.id,
            paidAmount = it.paidAmount,
            paymentDatetime = OffsetDateTime.parse(it.paymentDatetime),
            paymentMethod = it.paymentMethod,
            creator = FanboxCreator(
                creatorId = it.creator.creatorId?.let { id -> CreatorId(id) },
                user = FanboxUser(
                    userId = it.creator.user.userId,
                    creatorId = CreatorId(it.creator.creatorId.orEmpty()),
                    name = it.creator.user.name,
                    iconUrl = it.creator.user.iconUrl,
                ),
            ),
        )
    }
}

internal fun FanboxNewsLattersEntity.translate(): List<FanboxNewsLetter> {
    return body.map {
        FanboxNewsLetter(
            body = it.body,
            createdAt = it.createdAt,
            creator = FanboxCreator(
                creatorId = CreatorId(it.creator.creatorId),
                user = FanboxUser(
                    userId = it.creator.user.userId,
                    creatorId = CreatorId(it.creator.creatorId),
                    name = it.creator.user.name,
                    iconUrl = it.creator.user.iconUrl,
                ),
            ),
            id = NewsLetterId(it.id),
            isRead = it.isRead,
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
