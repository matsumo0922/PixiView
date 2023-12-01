package caios.android.fanbox.feature.post.search.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import caios.android.fanbox.core.common.util.suspendRunCatching
import caios.android.fanbox.core.model.fanbox.FanboxCreatorDetail
import caios.android.fanbox.core.repository.FanboxRepository

class PostSearchCreatorPagingSource(
    private val fanboxRepository: FanboxRepository,
    private val query: String,
) : PagingSource<Int, FanboxCreatorDetail>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FanboxCreatorDetail> {
        return suspendRunCatching {
            fanboxRepository.getCreatorFromQuery(query, params.key ?: 0)
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

    override fun getRefreshKey(state: PagingState<Int, FanboxCreatorDetail>): Int? = null
}
