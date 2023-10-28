package caios.android.pixiview.core.ui.extensition

import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

fun <T : Any> emptyPaging(): Flow<PagingData<T>> = flowOf(
    PagingData.empty(
        LoadStates(
            prepend = LoadState.NotLoading(true),
            append = LoadState.NotLoading(true),
            refresh = LoadState.NotLoading(true),
        )
    )
)
