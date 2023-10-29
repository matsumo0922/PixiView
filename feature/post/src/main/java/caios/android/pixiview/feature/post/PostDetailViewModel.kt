package caios.android.pixiview.feature.post

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import caios.android.pixiview.core.common.util.suspendRunCatching
import caios.android.pixiview.core.model.ScreenState
import caios.android.pixiview.core.model.fanbox.FanboxPost
import caios.android.pixiview.core.model.fanbox.FanboxPostDetail
import caios.android.pixiview.core.model.fanbox.id.PostId
import caios.android.pixiview.core.repository.FanboxRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val fanboxRepository: FanboxRepository,
): ViewModel() {

    private val _screenState = MutableStateFlow<ScreenState<PostDetailUiState>>(ScreenState.Loading)

    val screenState = _screenState.asStateFlow()

    fun fetch(postId: PostId) {
        viewModelScope.launch {
            _screenState.value = suspendRunCatching {
                fanboxRepository.getPost(postId)
            }.fold(
                onSuccess = { ScreenState.Idle(PostDetailUiState(it)) },
                onFailure = { ScreenState.Error(R.string.error_network) }
            )
        }
    }
}

@Stable
data class PostDetailUiState(
    val postDetail: FanboxPostDetail,
)
