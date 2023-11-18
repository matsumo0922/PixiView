package caios.android.pixiview.feature.post.liked

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import caios.android.pixiview.core.model.ScreenState
import caios.android.pixiview.core.model.UserData
import caios.android.pixiview.core.model.fanbox.FanboxPost
import caios.android.pixiview.core.repository.FanboxRepository
import caios.android.pixiview.core.repository.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LikedPostsViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val fanboxRepository: FanboxRepository,
) : ViewModel() {

    val screenState = combine(userDataRepository.userData, fanboxRepository.likedPosts) { userData, _ ->
        ScreenState.Idle(
            LikedPostsUiState(
                userData = userData,
                likedPosts = fanboxRepository.getLikedPosts(),
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ScreenState.Loading,
    )

    fun postLike(post: FanboxPost, isLiked: Boolean) {
        viewModelScope.launch {
            if (isLiked) {
                fanboxRepository.likePost(post)
            } else {
                fanboxRepository.unlikePost(post)
            }
        }
    }
}

@Stable
data class LikedPostsUiState(
    val userData: UserData,
    val likedPosts: List<FanboxPost>,
)
