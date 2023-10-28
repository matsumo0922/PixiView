package caios.android.pixiview.feature.welcome.login

import androidx.lifecycle.ViewModel
import caios.android.pixiview.core.repository.FanboxRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WelcomeLoginViewModel @Inject constructor(
    private val fanboxRepository: FanboxRepository,
) : ViewModel() {

    fun isLoggedIn(): Boolean {
        return fanboxRepository.hasActiveCookie()
    }
}
