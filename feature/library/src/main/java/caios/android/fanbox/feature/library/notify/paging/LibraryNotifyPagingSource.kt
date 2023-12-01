package caios.android.fanbox.feature.library.notify.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import caios.android.fanbox.core.common.util.suspendRunCatching
import caios.android.fanbox.core.model.fanbox.FanboxBell
import caios.android.fanbox.core.repository.FanboxRepository

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
