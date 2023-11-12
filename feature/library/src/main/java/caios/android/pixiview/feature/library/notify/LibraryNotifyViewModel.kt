package caios.android.pixiview.feature.library.notify

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import caios.android.pixiview.core.common.util.suspendRunCatching
import caios.android.pixiview.core.model.ScreenState
import caios.android.pixiview.core.model.fanbox.FanboxBell
import caios.android.pixiview.core.repository.FanboxRepository
import caios.android.pixiview.feature.library.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryNotifyViewModel @Inject constructor(
    private val fanboxRepository: FanboxRepository,
) : ViewModel() {

    private val _screenState = MutableStateFlow<ScreenState<LibraryNotifyUiState>>(ScreenState.Loading)

    val screenState = _screenState.asStateFlow()

    init {
        fetch()
    }

    fun fetch() {
        viewModelScope.launch {
            _screenState.value = ScreenState.Loading
            _screenState.value = suspendRunCatching {
                LibraryNotifyUiState(
                    bells = fanboxRepository.getBells(),
                )
            }.fold(
                onSuccess = { ScreenState.Idle(it) },
                onFailure = { ScreenState.Error(R.string.error_network) },
            )
        }
    }

    fun loadMore(page: Int) {
        (screenState.value as? ScreenState.Idle)?.also {
            viewModelScope.launch {
                _screenState.value = suspendRunCatching {
                    LibraryNotifyUiState(
                        bells = it.data.bells + fanboxRepository.getBells(page),
                    )
                }.fold(
                    onSuccess = { ScreenState.Idle(it) },
                    onFailure = { ScreenState.Error(R.string.error_network) },
                )
            }
        }
    }
}

@Stable
data class LibraryNotifyUiState(
    val bells: List<FanboxBell>,
)
