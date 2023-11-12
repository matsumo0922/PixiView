package caios.android.pixiview.feature.library.notify

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import caios.android.pixiview.core.model.fanbox.FanboxBell
import caios.android.pixiview.core.repository.FanboxRepository
import caios.android.pixiview.core.ui.extensition.emptyPaging
import caios.android.pixiview.feature.library.notify.paging.LibraryNotifyPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LibraryNotifyViewModel @Inject constructor(
    private val fanboxRepository: FanboxRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryNotifyUiState(emptyPaging()))

    val uiState = _uiState.asStateFlow()

    init {
        Pager(
            config = PagingConfig(pageSize = 10),
            initialKey = null,
            pagingSourceFactory = {
                LibraryNotifyPagingSource(fanboxRepository)
            },
        ).flow.cachedIn(viewModelScope).also {
            _uiState.value = LibraryNotifyUiState(it)
        }
    }
}

@Stable
data class LibraryNotifyUiState(
    val paging: Flow<PagingData<FanboxBell>>,
)
