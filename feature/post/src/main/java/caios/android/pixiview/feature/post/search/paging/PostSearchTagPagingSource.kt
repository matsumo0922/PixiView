package caios.android.pixiview.feature.post.search.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import caios.android.pixiview.core.common.util.suspendRunCatching
import caios.android.pixiview.core.model.fanbox.FanboxPost
import caios.android.pixiview.core.model.fanbox.id.CreatorId
import caios.android.pixiview.core.repository.FanboxRepository

class PostSearchTagPagingSource(
    private val fanboxRepository: FanboxRepository,
    private val creatorId: CreatorId?,
    private val tag: String,
) : PagingSource<Int, FanboxPost>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FanboxPost> {
        return suspendRunCatching {
            fanboxRepository.getPostFromQuery(tag, creatorId, params.key ?: 0)
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

    override fun getRefreshKey(state: PagingState<Int, FanboxPost>): Int? = null
}
