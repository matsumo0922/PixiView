package caios.android.pixiview.feature.creator.top

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import caios.android.pixiview.core.common.util.suspendRunCatching
import caios.android.pixiview.core.model.ScreenState
import caios.android.pixiview.core.model.UserData
import caios.android.pixiview.core.model.changeContent
import caios.android.pixiview.core.model.fanbox.FanboxCreatorDetail
import caios.android.pixiview.core.model.fanbox.FanboxCreatorPlan
import caios.android.pixiview.core.model.fanbox.FanboxCreatorTag
import caios.android.pixiview.core.model.fanbox.FanboxPost
import caios.android.pixiview.core.model.fanbox.id.CreatorId
import caios.android.pixiview.core.model.fanbox.id.PostId
import caios.android.pixiview.core.repository.FanboxRepository
import caios.android.pixiview.core.repository.UserDataRepository
import caios.android.pixiview.feature.creator.R
import caios.android.pixiview.feature.creator.top.paging.CreatorTopPostsPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatorTopViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val fanboxRepository: FanboxRepository,
) : ViewModel() {

    private val _screenState = MutableStateFlow<ScreenState<CreatorTopUiState>>(ScreenState.Loading)

    val screenState = _screenState.asStateFlow()

    private var postsPagingCache: Flow<PagingData<FanboxPost>>? = null

    init {
        viewModelScope.launch {
            userDataRepository.userData.collectLatest { data ->
                _screenState.value = screenState.value.changeContent { it.copy(userData = data) }
            }
        }

        viewModelScope.launch {
            fanboxRepository.bookmarkedPosts.collectLatest { data ->
                _screenState.value = screenState.value.changeContent { it.copy(bookmarkedPosts = data) }
            }
        }
    }

    fun fetch(creatorId: CreatorId) {
        viewModelScope.launch {
            _screenState.value = ScreenState.Loading
            _screenState.value = suspendRunCatching {
                CreatorTopUiState(
                    userData = userDataRepository.userData.first(),
                    bookmarkedPosts = fanboxRepository.bookmarkedPosts.first(),
                    creatorDetail = fanboxRepository.getCreator(creatorId),
                    creatorPlans = fanboxRepository.getCreatorPlans(creatorId),
                    creatorTags = fanboxRepository.getCreatorTags(creatorId),
                    creatorPostsPaging = postsPagingCache ?: buildPaging(creatorId).also { postsPagingCache = it },
                )
            }.fold(
                onSuccess = { ScreenState.Idle(it) },
                onFailure = { ScreenState.Error(R.string.error_network) },
            )
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

    fun postLike(postId: PostId) {
        viewModelScope.launch {
            suspendRunCatching {
                fanboxRepository.likePost(postId)
            }
        }
    }

    fun postBookmark(post: FanboxPost, isBookmarked: Boolean) {
        viewModelScope.launch {
            (screenState.value as? ScreenState.Idle)?.also {
                if (isBookmarked) {
                    fanboxRepository.bookmarkPost(post)
                } else {
                    fanboxRepository.unbookmarkPost(post)
                }
            }
        }
    }

    private fun buildPaging(creatorId: CreatorId): Flow<PagingData<FanboxPost>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            initialKey = null,
            pagingSourceFactory = {
                CreatorTopPostsPagingSource(
                    creatorId = creatorId,
                    fanboxRepository = fanboxRepository,
                )
            },
        ).flow.cachedIn(viewModelScope)
    }
}

@Stable
data class CreatorTopUiState(
    val userData: UserData,
    val bookmarkedPosts: List<PostId>,
    val creatorDetail: FanboxCreatorDetail,
    val creatorPlans: List<FanboxCreatorPlan>,
    val creatorTags: List<FanboxCreatorTag>,
    val creatorPostsPaging: Flow<PagingData<FanboxPost>>,
)
