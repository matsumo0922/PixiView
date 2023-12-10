package caios.android.fanbox.feature.library.discovery

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import caios.android.fanbox.core.common.util.suspendRunCatching
import caios.android.fanbox.core.model.ScreenState
import caios.android.fanbox.core.model.UserData
import caios.android.fanbox.core.model.changeContent
import caios.android.fanbox.core.model.fanbox.FanboxCreatorDetail
import caios.android.fanbox.core.repository.FanboxRepository
import caios.android.fanbox.core.repository.UserDataRepository
import caios.android.fanbox.core.ui.extensition.emptyPaging
import caios.android.fanbox.feature.library.R
import caios.android.fanbox.feature.library.discovery.paging.LibraryDiscoveryPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryDiscoveryViewModel @Inject constructor(
    private val fanboxRepository: FanboxRepository,
) : ViewModel() {

    private val _screenState = MutableStateFlow<ScreenState<LibraryDiscoveryUiState>>(ScreenState.Loading)

    val screenState = _screenState.asStateFlow()

    fun fetch() {
        viewModelScope.launch {
            _screenState.value = ScreenState.Loading
            _screenState.value = suspendRunCatching {
                LibraryDiscoveryUiState(
                    recommendedCreators = fanboxRepository.getRecommendedCreators(),
                    followingPixivCreators = fanboxRepository.getFollowingPixivCreators(),
                )
            }.fold(
                onSuccess = { ScreenState.Idle(it) },
                onFailure = { ScreenState.Error(R.string.error_no_data_discovery) }
            )
        }
    }

    suspend fun follow(creatorUserId: String): Result<Unit> {
        return suspendRunCatching {
            fanboxRepository.followCreator(creatorUserId)
        }
    }

    suspend fun unfollow(creatorUserId: String): Result<Unit> {
        return suspendRunCatching {
            fanboxRepository.unfollowCreator(creatorUserId)
        }
    }
}

@Stable
data class LibraryDiscoveryUiState(
    val recommendedCreators: List<FanboxCreatorDetail>,
    val followingPixivCreators: List<FanboxCreatorDetail>,
)
