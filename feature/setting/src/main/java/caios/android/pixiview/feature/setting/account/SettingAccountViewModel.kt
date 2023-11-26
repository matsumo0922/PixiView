package caios.android.pixiview.feature.setting.account

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import caios.android.pixiview.core.common.util.suspendRunCatching
import caios.android.pixiview.core.model.ScreenState
import caios.android.pixiview.core.model.fanbox.FanboxMetaData
import caios.android.pixiview.core.repository.FanboxRepository
import caios.android.pixiview.feature.setting.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingAccountViewModel @Inject constructor(
    private val fanboxRepository: FanboxRepository,
) : ViewModel() {

    private val _screenState = MutableStateFlow<ScreenState<SettingAccountUiState>>(ScreenState.Loading)

    val screenState = _screenState.asStateFlow()

    fun fetch() {
        viewModelScope.launch {
            _screenState.value = ScreenState.Loading
            _screenState.value = suspendRunCatching {
                fanboxRepository.updateCsrfToken()

                SettingAccountUiState(
                    userContext = fanboxRepository.metaData.first().context.user,
                )
            }.fold(
                onSuccess = { ScreenState.Idle(it) },
                onFailure = { ScreenState.Error(R.string.error_network) },
            )
        }
    }

    fun update() {
    }
}

@Stable
data class SettingAccountUiState(
    val userContext: FanboxMetaData.Context.User,
)
