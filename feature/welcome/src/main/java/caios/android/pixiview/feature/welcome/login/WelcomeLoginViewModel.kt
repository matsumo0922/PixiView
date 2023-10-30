package caios.android.pixiview.feature.welcome.login

import android.webkit.CookieManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import caios.android.pixiview.core.repository.FanboxRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeLoginViewModel @Inject constructor(
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
                fanboxRepository.updateCookie(it.orEmpty())
                fanboxRepository.getNewsLetters().also { newsLetters ->
                    _isLoggedInFlow.emit(newsLetters != null)
                }
            }
        }
    }
}
