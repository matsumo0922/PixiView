package caios.android.pixiview.feature.creator

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import caios.android.pixiview.core.common.util.suspendRunCatching
import caios.android.pixiview.core.model.ScreenState
import caios.android.pixiview.core.model.fanbox.FanboxCreatorDetail
import caios.android.pixiview.core.model.fanbox.FanboxCreatorPlan
import caios.android.pixiview.core.model.fanbox.FanboxPost
import caios.android.pixiview.core.model.fanbox.id.CreatorId
import caios.android.pixiview.core.repository.FanboxRepository
import caios.android.pixiview.feature.creator.paging.CreatorTopPostsPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatorTopViewModel @Inject constructor(
    private val fanboxRepository: FanboxRepository,
) : ViewModel() {

    private val _screenState = MutableStateFlow<ScreenState<CreatorTopUiState>>(ScreenState.Loading)

    val screenState = _screenState.asStateFlow()

    private var postsPagingCache: Flow<PagingData<FanboxPost>>? = null

    fun fetch(creatorId: CreatorId) {
        viewModelScope.launch {
            _screenState.value = ScreenState.Loading
            _screenState.value = suspendRunCatching {
                CreatorTopUiState(
                    creatorDetail = fanboxRepository.getCreator(creatorId),
                    creatorPlans = fanboxRepository.getCreatorPlans(creatorId),
                    creatorPostsPaging = postsPagingCache ?: buildPaging(creatorId).also { postsPagingCache = it },
                )
            }.fold(
                onSuccess = { ScreenState.Idle(it) },
                onFailure = { ScreenState.Error(R.string.error_network) },
            )
        }
    }

    private fun buildPaging(creatorId: CreatorId): Flow<PagingData<FanboxPost>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            initialKey = null,
            pagingSourceFactory = {
                CreatorTopPostsPagingSource(
                    creatorId = creatorId,
                    fanboxRepository = fanboxRepository,
                )
            },
        ).flow.cachedIn(viewModelScope)
    }
}

@Stable
data class CreatorTopUiState(
    val creatorDetail: FanboxCreatorDetail,
    val creatorPlans: List<FanboxCreatorPlan>,
    val creatorPostsPaging: Flow<PagingData<FanboxPost>>,
)
