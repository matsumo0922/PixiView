package caios.android.pixiview.feature.creator.follow

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import caios.android.pixiview.core.common.util.suspendRunCatching
import caios.android.pixiview.core.model.ScreenState
import caios.android.pixiview.core.model.fanbox.FanboxCreatorDetail
import caios.android.pixiview.core.repository.FanboxRepository
import caios.android.pixiview.feature.creator.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowingCreatorsViewModel @Inject constructor(
    private val fanboxRepository: FanboxRepository,
) : ViewModel() {

    private val _screenState = MutableStateFlow<ScreenState<FollowingCreatorsUiState>>(ScreenState.Loading)

    val screenState = _screenState.asStateFlow()

    init {
        fetch()
    }

    fun fetch() {
        viewModelScope.launch {
            _screenState.value = ScreenState.Loading
            _screenState.value = suspendRunCatching {
                FollowingCreatorsUiState(
                    followingCreators = fanboxRepository.getFollowingCreators(),
                )
            }.fold(
                onSuccess = { ScreenState.Idle(it) },
                onFailure = { ScreenState.Error(R.string.error_network) },
            )
        }
    }

    fun follow(creatorUserId: String) {
        viewModelScope.launch {
            (screenState.value as? ScreenState.Idle)?.also {
                val followingCreators = it.data.followingCreators.toMutableList()
                val index = followingCreators.indexOfFirst { creator -> creator.user.userId == creatorUserId }

                followingCreators[index] = suspendRunCatching {
                    fanboxRepository.followCreator(creatorUserId)
                }.fold(
                    onSuccess = { followingCreators[index].copy(isFollowed = true) },
                    onFailure = { followingCreators[index].copy(isFollowed = false) },
                )

                _screenState.value = ScreenState.Idle(it.data.copy(followingCreators = followingCreators))
            }
        }
    }

    fun unfollow(creatorUserId: String) {
        viewModelScope.launch {
            (screenState.value as? ScreenState.Idle)?.also {
                val followingCreators = it.data.followingCreators.toMutableList()
                val index = followingCreators.indexOfFirst { creator -> creator.user.userId == creatorUserId }

                followingCreators[index] = suspendRunCatching {
                    fanboxRepository.unfollowCreator(creatorUserId)
                }.fold(
                    onSuccess = { followingCreators[index].copy(isFollowed = false) },
                    onFailure = { followingCreators[index].copy(isFollowed = true) },
                )

                _screenState.value = ScreenState.Idle(it.data.copy(followingCreators = followingCreators))
            }
        }
    }
}

@Stable
data class FollowingCreatorsUiState(
    val followingCreators: List<FanboxCreatorDetail>,
)