package caios.android.fanbox.feature.library.home

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import caios.android.fanbox.core.common.util.suspendRunCatching
import caios.android.fanbox.core.model.UserData
import caios.android.fanbox.core.model.fanbox.FanboxPost
import caios.android.fanbox.core.model.fanbox.id.PostId
import caios.android.fanbox.core.repository.FanboxRepository
import caios.android.fanbox.core.repository.UserDataRepository
import caios.android.fanbox.core.ui.extensition.emptyPaging
import caios.android.fanbox.feature.library.home.paging.LibraryHomePagingSource
import caios.android.fanbox.feature.library.home.paging.LibrarySupportedPagingSource
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
        viewModelScope.launch {
            userDataRepository.userData.collectLatest { userData ->
                _uiState.value = uiState.value.copy(userData = userData)

                Pager(
                    config = PagingConfig(pageSize = if (userData.isHideRestricted || userData.isGridMode) 20 else 10),
                    initialKey = null,
                    pagingSourceFactory = {
                        LibraryHomePagingSource(fanboxRepository, userData.isHideRestricted)
                    },
                ).flow.cachedIn(viewModelScope).also {
                    _uiState.value = uiState.value.copy(homePaging = it)
                }

                Pager(
                    config = PagingConfig(pageSize =  if (userData.isHideRestricted || userData.isGridMode) 20 else 10),
                    initialKey = null,
                    pagingSourceFactory = {
                        LibrarySupportedPagingSource(fanboxRepository, userData.isHideRestricted)
                    },
                ).flow.cachedIn(viewModelScope).also {
                    _uiState.value = uiState.value.copy(supportedPaging = it)
                }
            }
        }

        viewModelScope.launch {
            fanboxRepository.bookmarkedPosts.collectLatest {
                _uiState.value = uiState.value.copy(bookmarkedPosts = it)
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
            suspendRunCatching {
                if (isBookmarked) {
                    fanboxRepository.bookmarkPost(post)
                } else {
                    fanboxRepository.unbookmarkPost(post)
                }
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
