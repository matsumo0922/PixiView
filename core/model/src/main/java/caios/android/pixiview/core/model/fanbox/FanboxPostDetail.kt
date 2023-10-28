package caios.android.pixiview.core.model.fanbox

import caios.android.pixiview.core.model.fanbox.entity.FanboxPostDetailEntity
import kotlinx.serialization.SerialName

data class FanboxPostDetail(
    val id: String,
    val title: String,
    val body: Body,
    val commentCount: Int,
    val commentList: Comment,
    val creatorId: String,
    val excerpt: String,
    val feeRequired: Int,
    val hasAdultContent: Boolean,
    val imageForShare: String,
    val isLiked: Boolean,
    val isRestricted: Boolean,
    val likeCount: Int,
    val tags: List<String>,
    val updatedDatetime: String,
    val publishedDatetime: String,
    val nextPost: OtherPost?,
    val prevPost: OtherPost?,
    val user: FanboxUser,
) {
    sealed interface Body {
        data class Article(val blocks: List<Block>) : Body {
            sealed interface Block {
                data class Text(val text: String) : Block

                data class Image(
                    val extension: String,
                    val originalUrl: String,
                    val thumbnailUrl: String,
                ) : Block

                data class File(
                    val name: String,
                    val extension: String,
                    val size: Long,
                    val url: String,
                ) : Block
            }
        }

        data class Image(
            val text: String,
            val images: List<ImageItem>
        ) : Body {
            data class ImageItem(
                val id: String,
                val extension: String,
                val originalUrl: String,
                val thumbnailUrl: String,
            )
        }

        data class File(
            val text: String,
            val files: List<FileItem>
        ) : Body {
            data class FileItem(
                val id: String,
                val name: String,
                val extension: String,
                val size: Long,
                val url: String,
            )
        }
    }

    data class Comment(
        val items: List<CommentItem>,
        val cursor: FanboxCursor?,
    ) {
        data class CommentItem(
            val body: String,
            val createdDatetime: String,
            val id: String,
            val isLiked: Boolean,
            val isOwn: Boolean,
            val likeCount: Int,
            val parentCommentId: String,
            val rootCommentId: String,
            val user: FanboxUser,
        )
    }

    data class OtherPost(
        val id: String,
        val title: String,
        val publishedDatetime: String,
    )
}
