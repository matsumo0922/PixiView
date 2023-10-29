package caios.android.pixiview.feature.post

import androidx.lifecycle.ViewModel
import caios.android.pixiview.core.repository.FanboxRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val fanboxRepository: FanboxRepository,
): ViewModel() {

}
