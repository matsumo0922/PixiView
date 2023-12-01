package caios.android.fanbox.feature.welcome.login.activity

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import caios.android.fanbox.core.common.util.suspendRunCatching
import caios.android.fanbox.core.model.ScreenState
import caios.android.fanbox.core.model.UserData
import caios.android.fanbox.core.model.pixiv.PixivAuthCode
import caios.android.fanbox.core.repository.FanboxRepository
import caios.android.fanbox.core.repository.PixivRepository
import caios.android.fanbox.core.repository.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel(
    private val userDataRepository: UserDataRepository,
    private val pixivRepository: PixivRepository,
    private val fanboxRepository: FanboxRepository,
    private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    val authCode = pixivRepository.getAuthCode()

    val screenState = userDataRepository.userData.map {
        ScreenState.Idle(
            LoginUiState(it),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ScreenState.Loading,
    )

    @Inject
    constructor(
        userDataRepository: UserDataRepository,
        pixivRepository: PixivRepository,
        fanboxRepository: FanboxRepository,
    ) : this(
        userDataRepository = userDataRepository,
        pixivRepository = pixivRepository,
        fanboxRepository = fanboxRepository,
        ioDispatcher = Dispatchers.IO,
    )

    suspend fun initAccount(code: PixivAuthCode) = withContext(ioDispatcher) {
        suspendRunCatching {
            pixivRepository.initAccount(code)!!
        }
    }

    fun updateCookie(cookie: String) {
        viewModelScope.launch {
            fanboxRepository.updateCookie(cookie)
        }
    }
}

@Stable
data class LoginUiState(
    val userData: UserData,
)
