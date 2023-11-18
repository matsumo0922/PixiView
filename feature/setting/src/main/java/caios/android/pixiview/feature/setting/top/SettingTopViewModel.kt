package caios.android.pixiview.feature.setting.top

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import caios.android.pixiview.core.common.PixiViewConfig
import caios.android.pixiview.core.model.ScreenState
import caios.android.pixiview.core.model.UserData
import caios.android.pixiview.core.model.fanbox.FanboxMetaData
import caios.android.pixiview.core.repository.FanboxRepository
import caios.android.pixiview.core.repository.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
@HiltViewModel
class SettingTopViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val fanboxRepository: FanboxRepository,
    private val pixiViewConfig: PixiViewConfig,
) : ViewModel() {

    val screenState = combine(userDataRepository.userData, fanboxRepository.cookie, fanboxRepository.metaData, ::Triple).map { (userData, cookie, metaData) ->
        val cookieMap = cookie.split(";")
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .associate { it.split("=", limit = 2).let { item -> item[0] to item[1] } }

        ScreenState.Idle(
            SettingTopUiState(
                userData = userData,
                metaData = metaData,
                fanboxSessionId = cookieMap.getOrDefault("FANBOXSESSID", "unknown"),
                config = pixiViewConfig,
            ),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ScreenState.Loading,
    )

    fun setFollowTabDefaultHome(isFollowTabDefaultHome: Boolean) {
        viewModelScope.launch {
            userDataRepository.setFollowTabDefaultHome(isFollowTabDefaultHome)
        }
    }

    fun setHideAdultContents(isHideAdultContents: Boolean) {
        viewModelScope.launch {
            userDataRepository.setHideAdultContents(isHideAdultContents)
        }
    }

    fun setDeveloperMode(isDeveloperMode: Boolean) {
        viewModelScope.launch {
            userDataRepository.setDeveloperMode(isDeveloperMode)
        }
    }
}

@Stable
data class SettingTopUiState(
    val userData: UserData,
    val metaData: FanboxMetaData,
    val fanboxSessionId: String,
    val config: PixiViewConfig,
)
