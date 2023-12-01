package caios.android.fanbox.feature.creator.fancard

import android.graphics.Bitmap
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import caios.android.fanbox.core.common.util.suspendRunCatching
import caios.android.fanbox.core.model.ScreenState
import caios.android.fanbox.core.model.fanbox.FanboxCreatorPlanDetail
import caios.android.fanbox.core.model.fanbox.id.CreatorId
import caios.android.fanbox.core.repository.FanboxRepository
import caios.android.fanbox.feature.creator.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FanCardViewModel @Inject constructor(
    private val fanboxRepository: FanboxRepository,
) : ViewModel() {

    private val _screenState = MutableStateFlow<ScreenState<FanCardUiState>>(ScreenState.Loading)
    private val _downloadedEvent = Channel<Boolean>()

    val screenState = _screenState.asStateFlow()
    val downloadedEvent = _downloadedEvent.receiveAsFlow()

    fun fetch(creatorId: CreatorId) {
        viewModelScope.launch {
            _screenState.value = ScreenState.Loading
            _screenState.value = suspendRunCatching {
                FanCardUiState(
                    planDetail = fanboxRepository.getCreatorPlan(creatorId),
                )
            }.fold(
                onSuccess = { ScreenState.Idle(it) },
                onFailure = {
                    ScreenState.Error(
                        message = R.string.creator_fan_card_not_supported,
                        retryTitle = R.string.common_back,
                    )
                },
            )
        }
    }

    fun download(bitmap: Bitmap, planDetail: FanboxCreatorPlanDetail) {
        viewModelScope.launch {
            suspendRunCatching {
                fanboxRepository.downloadBitmap(bitmap, "fancard-${planDetail.plan.user.creatorId}-${planDetail.plan.id}")
            }.fold(
                onSuccess = { _downloadedEvent.send(true) },
                onFailure = { _downloadedEvent.send(false) },
            )
        }
    }
}

@Stable
data class FanCardUiState(
    val planDetail: FanboxCreatorPlanDetail,
)
