package caios.android.fanbox.feature.welcome.login

import android.webkit.CookieManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import caios.android.fanbox.core.common.util.suspendRunCatching
import caios.android.fanbox.core.repository.FanboxRepository
import caios.android.fanbox.core.repository.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WelcomeLoginViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val fanboxRepository: FanboxRepository,
) : ViewModel() {

    private val _isLoggedInFlow = MutableSharedFlow<Boolean>(replay = 1)

    val isLoggedInFlow = _isLoggedInFlow.asSharedFlow()

    init {
        fetchLoggedIn()
    }

    fun fetchLoggedIn() {
        viewModelScope.launch {
            CookieManager.getInstance().getCookie("https://www.fanbox.cc/").also {
                suspendRunCatching {
                    fanboxRepository.updateCookie(it!!)
                    fanboxRepository.updateCsrfToken()

                    Timber.d("fetchLoggedIn: $it")

                    setDefaultHomeTab()
                }.isSuccess.also {
                    Timber.d("fetchLoggedIn: $it")
                    _isLoggedInFlow.emit(it)
                }
            }
        }
    }

    private suspend fun setDefaultHomeTab() {
        suspendRunCatching {
            fanboxRepository.getSupportedPosts()
        }.fold(
            onSuccess = { it.contents.isEmpty() },
            onFailure = { true }
        ).also {
            userDataRepository.setFollowTabDefaultHome(it)
        }
    }
}
