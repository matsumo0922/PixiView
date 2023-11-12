package caios.android.pixiview.core.model.fanbox

import caios.android.pixiview.core.model.fanbox.id.CommentId
import caios.android.pixiview.core.model.fanbox.id.CreatorId
import caios.android.pixiview.core.model.fanbox.id.PostId
import java.time.OffsetDateTime

sealed interface FanboxBell {
    val nextPage: Int?

    data class Comment(
        override val nextPage: Int?,
        val id: CommentId,
        val notifiedDatetime: OffsetDateTime,
        val comment: String,
        val isRootComment: Boolean,
        val creatorId: CreatorId,
        val postId: PostId,
        val postTitle: String,
        val userName: String,
        val userProfileIconUrl: String,
    ) : FanboxBell

    data class Like(
        override val nextPage: Int?,
        val id: String,
        val notifiedDatetime: OffsetDateTime,
        val comment: String,
        val creatorId: CreatorId,
        val postId: PostId,
        val count: Int,
    ) : FanboxBell

    data class PostPublished(
        override val nextPage: Int?,
        val id: PostId,
        val notifiedDatetime: OffsetDateTime,
        val post: FanboxPost,
    ) : FanboxBell
}
