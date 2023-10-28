package caios.android.pixiview.feature.welcome.login

import androidx.lifecycle.ViewModel
import caios.android.pixiview.core.repository.FanboxRepository
import caios.android.pixiview.core.repository.PixivRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WelcomeLoginViewModel @Inject constructor(
    private val pixivRepository: PixivRepository,
    private val fanboxRepository: FanboxRepository,
): ViewModel() {

    fun isLoggedIn(): Boolean {
        return pixivRepository.hasActiveAccount() && fanboxRepository.hasActiveCookie()
    }
}
