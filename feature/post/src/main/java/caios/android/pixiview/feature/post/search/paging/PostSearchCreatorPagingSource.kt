package caios.android.pixiview.feature.post.search.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import caios.android.pixiview.core.common.util.suspendRunCatching
import caios.android.pixiview.core.model.fanbox.FanboxCreatorDetail
import caios.android.pixiview.core.repository.FanboxRepository

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
