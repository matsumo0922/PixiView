package caios.android.fanbox

import android.webkit.CookieManager
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import caios.android.fanbox.core.billing.BillingClient
import caios.android.fanbox.core.common.util.suspendRunCatching
import caios.android.fanbox.core.model.ScreenState
import caios.android.fanbox.core.model.UserData
import caios.android.fanbox.core.model.fanbox.FanboxMetaData
import caios.android.fanbox.core.repository.FanboxRepository
import caios.android.fanbox.core.repository.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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

    private val _isLoggedInFlow: MutableSharedFlow<Boolean> = MutableSharedFlow(replay = 1)

    private val _isAppLockedFlow: MutableStateFlow<Boolean> = MutableStateFlow(true)

    val screenState = combine(
        userDataRepository.userData,
        fanboxRepository.cookie,
        fanboxRepository.metaData,
        _isLoggedInFlow,
        _isAppLockedFlow,
    ) { userData, cookie, metadata, isLoggedIn, isAppLocked ->
        ScreenState.Idle(
            MainUiState(
                userData = userData,
                fanboxCookie = cookie,
                fanboxMetadata = metadata,
                isLoggedIn = isLoggedIn,
                isAppLocked = if (userData.isAppLock) isAppLocked else false,
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
                _isLoggedInFlow.emit(false)
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
                    fanboxRepository.updateCookie(it!!)
                    fanboxRepository.updateCsrfToken()

                    fanboxRepository.metaData.firstOrNull()?.also {
                        userDataRepository.setTestUser(it.context.user.userId == "100912340")
                    }
                }.isSuccess.also {
                    _isLoggedInFlow.emit(it)
                }
            }
        }
    }

    fun setAppLock(isAppLock: Boolean) {
        viewModelScope.launch {
            _isAppLockedFlow.emit(isAppLock)
        }
    }
}

@Stable
data class MainUiState(
    val userData: UserData,
    val fanboxCookie: String,
    val fanboxMetadata: FanboxMetaData,
    val isLoggedIn: Boolean,
    val isAppLocked: Boolean,
)
