package caios.android.pixiview

import android.webkit.CookieManager
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import caios.android.pixiview.core.model.ScreenState
import caios.android.pixiview.core.model.UserData
import caios.android.pixiview.core.repository.FanboxRepository
import caios.android.pixiview.core.repository.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

@Stable
@HiltViewModel
class MainViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val fanboxRepository: FanboxRepository,
) : ViewModel() {

    val screenState = userDataRepository.userData.map {
        ScreenState.Idle(
            MainUiState(it),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ScreenState.Loading,
    )

    init {
        viewModelScope.launch {
            CookieManager.getInstance().getCookie("https://www.fanbox.cc/")?.also {
                fanboxRepository.updateCookie(it)
                val a = fanboxRepository.getCreator("kiyo")
                val b = fanboxRepository.getCreatorTags("kiyo")

                Timber.d("test: $a, $b")
            }
        }
    }

    fun initPixiViewId() {
        viewModelScope.launch {
            userDataRepository.setPixiViewId(UUID.randomUUID().toString())
        }
    }

    fun isLoggedIn(): Boolean {
        return fanboxRepository.hasActiveCookie()
    }
}

@Stable
data class MainUiState(
    val userData: UserData,
)
