package caios.android.fanbox.feature.library.discovery.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import caios.android.fanbox.core.common.util.suspendRunCatching
import caios.android.fanbox.core.model.fanbox.FanboxCreatorDetail
import caios.android.fanbox.core.repository.FanboxRepository

class LibraryDiscoveryPagingSource(
    private val fanboxRepository: FanboxRepository,
) : PagingSource<Int, FanboxCreatorDetail>() {

    private var currentSize = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FanboxCreatorDetail> {
        return suspendRunCatching {
            fanboxRepository.getRecommendedCreators()
        }.fold(
            onSuccess = {
                currentSize += it.size

                LoadResult.Page(
                    data = it,
                    nextKey = if (currentSize < MAX_SIZE) currentSize else null,
                    prevKey = null,
                )
            },
            onFailure = {
                LoadResult.Error(it)
            },
        )
    }

    override fun getRefreshKey(state: PagingState<Int, FanboxCreatorDetail>): Int? = null

    companion object {
        private const val MAX_SIZE = 100
    }
}
