package caios.android.pixiview.feature.creator.download

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import caios.android.pixiview.core.common.util.suspendRunCatching
import caios.android.pixiview.core.model.fanbox.FanboxPost
import caios.android.pixiview.core.model.fanbox.id.CreatorId
import caios.android.pixiview.core.repository.FanboxRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import javax.inject.Inject

@HiltViewModel
class CreatorPostsDownloadViewModel @Inject constructor(
    private val fanboxRepository: FanboxRepository,
) : ViewModel() {

    private val dispatcher = Executors.newFixedThreadPool(5).asCoroutineDispatcher()

    private val _uiState = MutableStateFlow(CreatorPostsDownloadUiState(emptyList(), false))

    val uiState = _uiState.asStateFlow()

    fun fetch(creatorId: CreatorId) {
        viewModelScope.launch {
            _uiState.value = suspendRunCatching {
                val paginate = fanboxRepository.getCreatorPostsPaginate(creatorId)
                val posts = paginate.map { async(dispatcher) { fanboxRepository.getCreatorPosts(creatorId, it) } }

                posts.map { it.await().contents }.flatten()
            }.fold(
                onSuccess = { CreatorPostsDownloadUiState(it, true) },
                onFailure = { CreatorPostsDownloadUiState(emptyList(), false) }
            )
        }
    }
}

@Stable
data class CreatorPostsDownloadUiState(
    val posts: List<FanboxPost>,
    val isReady: Boolean,
)
