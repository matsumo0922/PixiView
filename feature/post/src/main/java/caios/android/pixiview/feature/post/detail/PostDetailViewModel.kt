package caios.android.pixiview.feature.post.detail

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import caios.android.pixiview.core.common.util.suspendRunCatching
import caios.android.pixiview.core.model.PageOffsetInfo
import caios.android.pixiview.core.model.ScreenState
import caios.android.pixiview.core.model.UserData
import caios.android.pixiview.core.model.changeContent
import caios.android.pixiview.core.model.fanbox.FanboxCreatorDetail
import caios.android.pixiview.core.model.fanbox.FanboxMetaData
import caios.android.pixiview.core.model.fanbox.FanboxPost
import caios.android.pixiview.core.model.fanbox.FanboxPostDetail
import caios.android.pixiview.core.model.fanbox.id.CommentId
import caios.android.pixiview.core.model.fanbox.id.PostId
import caios.android.pixiview.core.repository.FanboxRepository
import caios.android.pixiview.core.repository.UserDataRepository
import caios.android.pixiview.feature.post.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val fanboxRepository: FanboxRepository,
) : ViewModel() {

    private val _screenState = MutableStateFlow<ScreenState<PostDetailUiState>>(ScreenState.Loading)

    val screenState = _screenState.asStateFlow()

    init {
        viewModelScope.launch {
            userDataRepository.userData.collectLatest { data ->
                _screenState.value = screenState.value.changeContent { it.copy(userData = data) }
            }
        }

        viewModelScope.launch {
            fanboxRepository.bookmarkedPosts.collectLatest { bookmarkedPosts ->
                _screenState.value = screenState.value.changeContent {
                    it.copy(postDetail = it.postDetail.copy(isBookmarked = it.postDetail.id in bookmarkedPosts))
                }
            }
        }
    }

    fun fetch(postId: PostId) {
        viewModelScope.launch {
            _screenState.value = ScreenState.Loading
            _screenState.value = suspendRunCatching {
                val postDetail = fanboxRepository.getPost(postId)
                val creatorDetail = fanboxRepository.getCreatorCached(postDetail.user.creatorId)

                PostDetailUiState(
                    userData = userDataRepository.userData.first(),
                    metaData = fanboxRepository.metaData.first(),
                    postDetail = postDetail,
                    creatorDetail = creatorDetail,
                )
            }.fold(
                onSuccess = { ScreenState.Idle(it) },
                onFailure = { ScreenState.Error(R.string.error_network) },
            )
        }

        viewModelScope.launch {
            fanboxRepository.bookmarkedPosts.collectLatest { bookmarkedPosts ->
                _screenState.value = screenState.value.changeContent {
                    it.copy(postDetail = it.postDetail.copy(isBookmarked = it.postDetail.id in bookmarkedPosts))
                }
            }
        }
    }

    fun postBookmark(post: FanboxPost, isBookmarked: Boolean) {
        viewModelScope.launch {
            if (isBookmarked) {
                fanboxRepository.bookmarkPost(post)
            } else {
                fanboxRepository.unbookmarkPost(post)
            }
        }
    }

    fun loadMoreComment(postId: PostId, offset: Int) {
        viewModelScope.launch {
            val comments = fanboxRepository.getPostComment(postId, offset)

            _screenState.value = screenState.value.changeContent {
                it.copy(
                    postDetail = it.postDetail.copy(
                        commentList = PageOffsetInfo(
                            contents = it.postDetail.commentList.contents + comments.contents,
                            offset = comments.offset,
                        ),
                    ),
                )
            }
        }
    }

    fun commentLike(commentId: CommentId) {
        viewModelScope.launch {
            suspendRunCatching {
                fanboxRepository.likeComment(commentId)
            }
        }
    }

    fun commentReply(postId: PostId, body: String, parentCommentId: CommentId, rootCommentId: CommentId) {
        viewModelScope.launch {
            (screenState.value as? ScreenState.Idle)?.also { data ->
                _screenState.value = suspendRunCatching {
                    fanboxRepository.addComment(postId, body, rootCommentId, parentCommentId)
                    fanboxRepository.getPost(postId)
                }.fold(
                    onSuccess = {
                        ScreenState.Idle(
                            data.data.copy(
                                postDetail = it,
                                messageToast = R.string.post_detail_comment_commented,
                            ),
                        )
                    },
                    onFailure = {
                        ScreenState.Idle(
                            data.data.copy(
                                messageToast = R.string.post_detail_comment_comment_failed,
                            ),
                        )
                    },
                )
            }
        }
    }

    fun commentDelete(commentId: CommentId) {
        viewModelScope.launch {
            (screenState.value as? ScreenState.Idle)?.also { data ->
                _screenState.value = suspendRunCatching {
                    fanboxRepository.deleteComment(commentId)
                    fanboxRepository.getPost(data.data.postDetail.id)
                }.fold(
                    onSuccess = {
                        ScreenState.Idle(
                            data.data.copy(
                                postDetail = it,
                                messageToast = R.string.post_detail_comment_delete_success,
                            ),
                        )
                    },
                    onFailure = {
                        ScreenState.Idle(
                            data.data.copy(
                                messageToast = R.string.post_detail_comment_delete_failed,
                            ),
                        )
                    },
                )
            }
        }
    }

    fun follow(creatorUserId: String) {
        viewModelScope.launch {
            (screenState.value as? ScreenState.Idle)?.also { data ->
                val creatorDetail = suspendRunCatching {
                    fanboxRepository.followCreator(creatorUserId)
                }.fold(
                    onSuccess = { data.data.creatorDetail.copy(isFollowed = true) },
                    onFailure = { data.data.creatorDetail.copy(isFollowed = false) },
                )

                _screenState.value = ScreenState.Idle(data.data.copy(creatorDetail = creatorDetail))
            }
        }
    }

    fun unfollow(creatorUserId: String) {
        viewModelScope.launch {
            (screenState.value as? ScreenState.Idle)?.also { data ->
                val creatorDetail = suspendRunCatching {
                    fanboxRepository.unfollowCreator(creatorUserId)
                }.fold(
                    onSuccess = { data.data.creatorDetail.copy(isFollowed = false) },
                    onFailure = { data.data.creatorDetail.copy(isFollowed = true) },
                )

                _screenState.value = ScreenState.Idle(data.data.copy(creatorDetail = creatorDetail))
            }
        }
    }

    fun consumeToast() {
        viewModelScope.launch {
            (screenState.value as? ScreenState.Idle)?.also { data ->
                _screenState.value = ScreenState.Idle(data.data.copy(messageToast = null))
            }
        }
    }
}

@Stable
data class PostDetailUiState(
    val userData: UserData,
    val metaData: FanboxMetaData,
    val creatorDetail: FanboxCreatorDetail,
    val postDetail: FanboxPostDetail,
    val messageToast: Int? = null,
)
