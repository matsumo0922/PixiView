package caios.android.pixiview.feature.creator.payment

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import caios.android.pixiview.core.common.util.suspendRunCatching
import caios.android.pixiview.core.model.ScreenState
import caios.android.pixiview.core.model.fanbox.FanboxPaidRecord
import caios.android.pixiview.core.repository.FanboxRepository
import caios.android.pixiview.feature.creator.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import javax.inject.Inject

@HiltViewModel
class PaymentsViewModel @Inject constructor(
    private val fanboxRepository: FanboxRepository,
) : ViewModel() {

    private val _screenState = MutableStateFlow<ScreenState<PaymentsUiState>>(ScreenState.Loading)

    val screenState = _screenState.asStateFlow()

    init {
        fetch()
    }

    fun fetch() {
        viewModelScope.launch {
            _screenState.value = ScreenState.Loading
            _screenState.value = suspendRunCatching {
                PaymentsUiState(
                    payments = fanboxRepository.getPaidRecords().translate(),
                )
            }.fold(
                onSuccess = { ScreenState.Idle(it) },
                onFailure = { ScreenState.Error(R.string.error_network) },
            )
        }
    }

    private fun List<FanboxPaidRecord>.translate(): List<Payment> {
        val paymentDates = map { it.paymentDateTime }.distinctBy { "${it.year}:${it.dayOfYear}" }

        return paymentDates.map { paymentDate ->
            Payment(
                paymentDateTime = paymentDate,
                paidRecords = filter {
                    it.paymentDateTime.year == paymentDate.year && it.paymentDateTime.dayOfYear == paymentDate.dayOfYear
                },
            )
        }
    }
}

@Stable
data class PaymentsUiState(
    val payments: List<Payment>,
)

@Stable
data class Payment(
    val paymentDateTime: OffsetDateTime,
    val paidRecords: List<FanboxPaidRecord>,
)
