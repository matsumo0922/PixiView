package caios.android.fanbox.feature.library.discovery

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import caios.android.fanbox.core.common.util.suspendRunCatching
import caios.android.fanbox.core.model.UserData
import caios.android.fanbox.core.model.fanbox.FanboxCreatorDetail
import caios.android.fanbox.core.repository.FanboxRepository
import caios.android.fanbox.core.repository.UserDataRepository
import caios.android.fanbox.core.ui.extensition.emptyPaging
import caios.android.fanbox.feature.library.discovery.paging.LibraryDiscoveryPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryDiscoveryViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val fanboxRepository: FanboxRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        LibraryDiscoveryUiState(
            userData = UserData.dummy(),
            paging = emptyPaging(),
        )
    )

    val uiState = _uiState.asStateFlow()

    init {
        Pager(
            config = PagingConfig(pageSize = 10),
            initialKey = null,
            pagingSourceFactory = {
                LibraryDiscoveryPagingSource(fanboxRepository)
            },
        ).flow.cachedIn(viewModelScope).also {
            _uiState.value = uiState.value.copy(paging = it)
        }

        viewModelScope.launch {
            userDataRepository.userData.collectLatest {
                _uiState.value = uiState.value.copy(userData = it)
            }
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
    val userData: UserData,
    val paging: Flow<PagingData<FanboxCreatorDetail>>,
)
