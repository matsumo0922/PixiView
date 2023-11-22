package caios.android.pixiview.feature.post.detail

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import caios.android.pixiview.core.common.util.suspendRunCatching
import caios.android.pixiview.core.model.ScreenState
import caios.android.pixiview.core.model.UserData
import caios.android.pixiview.core.model.changeContent
import caios.android.pixiview.core.model.fanbox.FanboxPost
import caios.android.pixiview.core.model.fanbox.FanboxPostDetail
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
    }

    fun fetch(postId: PostId) {
        viewModelScope.launch {
            _screenState.value = ScreenState.Loading
            _screenState.value = suspendRunCatching {
                PostDetailUiState(
                    userData = userDataRepository.userData.first(),
                    postDetail = fanboxRepository.getPost(postId),
                )
            }.fold(
                onSuccess = { ScreenState.Idle(it) },
                onFailure = { ScreenState.Error(R.string.error_network) },
            )
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
}

@Stable
data class PostDetailUiState(
    val userData: UserData,
    val postDetail: FanboxPostDetail,
    val messageToast: Int? = null,
)
