package caios.android.pixiview.core.model.fanbox

import caios.android.pixiview.core.model.fanbox.id.CommentId
import caios.android.pixiview.core.model.fanbox.id.CreatorId
import caios.android.pixiview.core.model.fanbox.id.PostId
import java.time.OffsetDateTime

sealed interface FanboxBell {
    data class Comment(
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
        val id: String,
        val notifiedDatetime: OffsetDateTime,
        val comment: String,
        val creatorId: CreatorId,
        val postId: PostId,
        val count: Int,
    ) : FanboxBell

    data class PostPublished(
        val id: PostId,
        val notifiedDatetime: OffsetDateTime,
        val post: FanboxPost,
    ) : FanboxBell
}
