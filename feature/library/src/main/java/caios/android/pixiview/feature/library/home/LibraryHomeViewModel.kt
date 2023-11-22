package caios.android.pixiview.feature.library.home

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import caios.android.pixiview.core.model.UserData
import caios.android.pixiview.core.model.fanbox.FanboxPost
import caios.android.pixiview.core.model.fanbox.id.PostId
import caios.android.pixiview.core.repository.FanboxRepository
import caios.android.pixiview.core.repository.UserDataRepository
import caios.android.pixiview.core.ui.extensition.emptyPaging
import caios.android.pixiview.feature.library.home.paging.LibraryHomePagingSource
import caios.android.pixiview.feature.library.home.paging.LibrarySupportedPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryHomeViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val fanboxRepository: FanboxRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        LibraryUiState(
            userData = UserData.dummy(),
            bookmarkedPosts = emptyList(),
            homePaging = emptyPaging(),
            supportedPaging = emptyPaging(),
        ),
    )

    val uiState = _uiState.asStateFlow()

    init {
        Pager(
            config = PagingConfig(pageSize = 10),
            initialKey = null,
            pagingSourceFactory = {
                LibraryHomePagingSource(fanboxRepository)
            },
        ).flow.cachedIn(viewModelScope).also {
            _uiState.value = uiState.value.copy(homePaging = it)
        }

        Pager(
            config = PagingConfig(pageSize = 10),
            initialKey = null,
            pagingSourceFactory = {
                LibrarySupportedPagingSource(fanboxRepository)
            },
        ).flow.cachedIn(viewModelScope).also {
            _uiState.value = uiState.value.copy(supportedPaging = it)
        }

        viewModelScope.launch {
            userDataRepository.userData.collectLatest {
                _uiState.value = uiState.value.copy(userData = it)
            }
        }

        viewModelScope.launch {
            fanboxRepository.bookmarkedPosts.collectLatest {
                _uiState.value = uiState.value.copy(bookmarkedPosts = it)
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
}

@Stable
data class LibraryUiState(
    val userData: UserData,
    val bookmarkedPosts: List<PostId>,
    val homePaging: Flow<PagingData<FanboxPost>>,
    val supportedPaging: Flow<PagingData<FanboxPost>>,
)
