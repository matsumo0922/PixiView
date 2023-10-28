package caios.android.pixiview.feature.library.home

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import caios.android.pixiview.core.common.util.suspendRunCatching
import caios.android.pixiview.core.model.ScreenState
import caios.android.pixiview.core.model.fanbox.FanboxCursor
import caios.android.pixiview.core.model.fanbox.FanboxPost
import caios.android.pixiview.core.repository.FanboxRepository
import caios.android.pixiview.feature.library.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryHomeViewModel @Inject constructor(
    private val fanboxRepository: FanboxRepository,
) : ViewModel() {

    private val _homeScreenState = MutableStateFlow<ScreenState<LibraryHomeUiState>>(ScreenState.Loading)
    private val _supportedScreenState = MutableStateFlow<ScreenState<LibrarySupportedUiState>>(ScreenState.Loading)

    val homeScreenState = _homeScreenState.asStateFlow()
    val supportedScreenState = _supportedScreenState.asStateFlow()

    init {
        initialize()
    }

    fun initialize() {
        
    }
}

@Stable
data class LibraryHomeUiState(
    val paging: Flow<PagingData<FanboxPost>>
)

@Stable
data class LibrarySupportedUiState(
    val paging: Flow<PagingData<FanboxPost>>
)
