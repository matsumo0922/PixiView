package caios.android.pixiview

import android.webkit.CookieManager
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import caios.android.pixiview.core.billing.BillingClient
import caios.android.pixiview.core.common.util.suspendRunCatching
import caios.android.pixiview.core.model.ScreenState
import caios.android.pixiview.core.model.UserData
import caios.android.pixiview.core.repository.FanboxRepository
import caios.android.pixiview.core.repository.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Timer
import java.util.UUID
import javax.inject.Inject
import kotlin.concurrent.schedule
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit

@Stable
@HiltViewModel
class MainViewModel @Inject constructor(
    billingClient: BillingClient,
    private val userDataRepository: UserDataRepository,
    private val fanboxRepository: FanboxRepository,
) : ViewModel() {

    private val isLoggedInFlow: MutableSharedFlow<Boolean> = MutableSharedFlow(replay = 1)

    val screenState = combine(userDataRepository.userData, fanboxRepository.cookie, isLoggedInFlow, ::Triple).map { (userData, cookie, isLoggedIn) ->
        ScreenState.Idle(
            MainUiState(
                userData = userData,
                fanboxCookie = cookie,
                isLoggedIn = isLoggedIn,
            ),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ScreenState.Loading,
    )

    init {
        billingClient.initialize()

        viewModelScope.launch {
            fanboxRepository.logoutTrigger.collectLatest {
                Timber.d("logoutTrigger")
                isLoggedInFlow.emit(false)
            }
        }

        Timer().schedule(0, 10.minutes.toLong(DurationUnit.MILLISECONDS)) {
            updateState()
        }
    }

    fun initPixiViewId() {
        viewModelScope.launch {
            userDataRepository.setPixiViewId(UUID.randomUUID().toString())
        }
    }

    fun updateState() {
        viewModelScope.launch {
            CookieManager.getInstance().getCookie("https://www.fanbox.cc/").also {
                suspendRunCatching {
                    fanboxRepository.updateCookie(it.orEmpty())
                    fanboxRepository.updateCsrfToken()
                }.isSuccess.also {
                    isLoggedInFlow.emit(it)
                }
            }
        }
    }
}

@Stable
data class MainUiState(
    val userData: UserData,
    val fanboxCookie: String,
    val isLoggedIn: Boolean,
)
