package caios.android.fanbox.feature.creator.support

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import caios.android.fanbox.core.common.util.suspendRunCatching
import caios.android.fanbox.core.model.ScreenState
import caios.android.fanbox.core.model.fanbox.FanboxCreatorPlan
import caios.android.fanbox.core.repository.FanboxRepository
import caios.android.fanbox.feature.creator.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SupportingCreatorsViewModel @Inject constructor(
    private val fanboxRepository: FanboxRepository,
) : ViewModel() {

    private val _screenState = MutableStateFlow<ScreenState<SupportingCreatorsUiState>>(ScreenState.Loading)

    val screenState = _screenState.asStateFlow()

    init {
        fetch()
    }

    fun fetch() {
        viewModelScope.launch {
            _screenState.value = ScreenState.Loading
            _screenState.value = suspendRunCatching {
                SupportingCreatorsUiState(
                    supportedPlans = fanboxRepository.getSupportedPlans(),
                )
            }.fold(
                onSuccess = { ScreenState.Idle(it) },
                onFailure = { ScreenState.Error(R.string.error_network) },
            )
        }
    }
}

@Stable
data class SupportingCreatorsUiState(
    val supportedPlans: List<FanboxCreatorPlan>,
)
