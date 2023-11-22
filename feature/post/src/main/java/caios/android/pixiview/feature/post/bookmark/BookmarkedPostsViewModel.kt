package caios.android.pixiview.feature.post.bookmark

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

    val screenState = combine(userDataRepository.userData, fanboxRepository.bookmarkedPosts) { userData, _ ->
        ScreenState.Idle(
            LikedPostsUiState(
                userData = userData,
                bookmarkedPosts = fanboxRepository.getBookmarkedPosts(),
            ),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ScreenState.Loading,
    )

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
data class LikedPostsUiState(
    val userData: UserData,
    val bookmarkedPosts: List<FanboxPost>,
)
