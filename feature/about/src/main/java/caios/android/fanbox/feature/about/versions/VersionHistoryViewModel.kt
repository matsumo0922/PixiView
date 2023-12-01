package caios.android.fanbox.feature.about.versions

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import caios.android.fanbox.core.common.util.suspendRunCatching
import caios.android.fanbox.core.datastore.PreferenceVersion
import caios.android.fanbox.core.model.ScreenState
import caios.android.fanbox.core.model.Version
import caios.android.fanbox.feature.about.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VersionHistoryViewModel @Inject constructor(
    private val versionPreference: PreferenceVersion,
) : ViewModel() {

    private val _screenState = MutableStateFlow<ScreenState<VersionHistoryUiState>>(ScreenState.Loading)

    val screenState = _screenState.asStateFlow()

    init {
        fetch()
    }

    fun fetch() {
        viewModelScope.launch {
            _screenState.value = ScreenState.Loading
            _screenState.value = suspendRunCatching {
                VersionHistoryUiState(
                    versions = versionPreference.get(),
                )
            }.fold(
                onSuccess = { ScreenState.Idle(it) },
                onFailure = { ScreenState.Error(R.string.error_no_data, R.string.common_close) },
            )
        }
    }
}

@Stable
data class VersionHistoryUiState(
    val versions: List<Version>,
)
