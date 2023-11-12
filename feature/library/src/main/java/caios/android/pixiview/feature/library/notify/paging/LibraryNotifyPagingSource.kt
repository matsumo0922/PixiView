package caios.android.pixiview.feature.library.notify.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import caios.android.pixiview.core.common.util.suspendRunCatching
import caios.android.pixiview.core.model.fanbox.FanboxBell
import caios.android.pixiview.core.repository.FanboxRepository

class LibraryNotifyPagingSource(
    private val fanboxRepository: FanboxRepository,
) : PagingSource<Int, FanboxBell>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FanboxBell> {
        return suspendRunCatching {
            fanboxRepository.getBells(params.key ?: 1)
        }.fold(
            onSuccess = {
                LoadResult.Page(
                    data = it.contents,
                    nextKey = it.nextPage,
                    prevKey = null,
                )
            },
            onFailure = {
                LoadResult.Error(it)
            },
        )
    }

    override fun getRefreshKey(state: PagingState<Int, FanboxBell>): Int? = null
}
