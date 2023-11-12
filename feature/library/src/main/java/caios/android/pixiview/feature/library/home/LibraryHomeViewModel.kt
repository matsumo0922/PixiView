package caios.android.pixiview.feature.library.home

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import caios.android.pixiview.core.model.fanbox.FanboxPost
import caios.android.pixiview.core.repository.FanboxRepository
import caios.android.pixiview.core.ui.extensition.emptyPaging
import caios.android.pixiview.feature.library.home.paging.LibraryHomePagingSource
import caios.android.pixiview.feature.library.home.paging.LibrarySupportedPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LibraryHomeViewModel @Inject constructor(
    private val fanboxRepository: FanboxRepository,
) : ViewModel() {

    private val _homeUiState = MutableStateFlow(LibraryHomeUiState(emptyPaging()))
    private val _supportedUiState = MutableStateFlow(LibrarySupportedUiState(emptyPaging()))

    val homeUiState = _homeUiState.asStateFlow()
    val supportedUiState = _supportedUiState.asStateFlow()

    init {
        Pager(
            config = PagingConfig(pageSize = 10),
            initialKey = null,
            pagingSourceFactory = {
                LibraryHomePagingSource(fanboxRepository)
            },
        ).flow.cachedIn(viewModelScope).also {
            _homeUiState.value = LibraryHomeUiState(it)
        }

        Pager(
            config = PagingConfig(pageSize = 10),
            initialKey = null,
            pagingSourceFactory = {
                LibrarySupportedPagingSource(fanboxRepository)
            },
        ).flow.cachedIn(viewModelScope).also {
            _supportedUiState.value = LibrarySupportedUiState(it)
        }
    }
}

@Stable
data class LibraryHomeUiState(
    val paging: Flow<PagingData<FanboxPost>>,
)

@Stable
data class LibrarySupportedUiState(
    val paging: Flow<PagingData<FanboxPost>>,
)
