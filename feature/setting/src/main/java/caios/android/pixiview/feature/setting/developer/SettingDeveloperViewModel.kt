package caios.android.pixiview.feature.setting.developer

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import caios.android.pixiview.core.common.PixiViewConfig
import caios.android.pixiview.core.repository.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
@HiltViewModel
class SettingDeveloperViewModel @Inject constructor(
    private val pixiViewConfig: PixiViewConfig,
    private val userDataRepository: UserDataRepository,
) : ViewModel() {

    fun submitPassword(password: String): Boolean {
        if (password == pixiViewConfig.developerPassword) {
            viewModelScope.launch { userDataRepository.setDeveloperMode(true) }
            return true
        }

        return false
    }
}
