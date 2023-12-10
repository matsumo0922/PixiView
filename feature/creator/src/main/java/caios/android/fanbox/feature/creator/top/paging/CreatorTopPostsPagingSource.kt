package caios.android.fanbox.feature.creator.top.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import caios.android.fanbox.core.common.util.suspendRunCatching
import caios.android.fanbox.core.model.fanbox.FanboxCursor
import caios.android.fanbox.core.model.fanbox.FanboxPost
import caios.android.fanbox.core.model.fanbox.id.CreatorId
import caios.android.fanbox.core.repository.FanboxRepository

class CreatorTopPostsPagingSource(
    private val creatorId: CreatorId,
    private val fanboxRepository: FanboxRepository,
) : PagingSource<FanboxCursor, FanboxPost>() {

    override suspend fun load(params: LoadParams<FanboxCursor>): LoadResult<FanboxCursor, FanboxPost> {
        return suspendRunCatching {
            fanboxRepository.getCreatorPosts(creatorId, params.key, params.loadSize)
        }.fold(
            onSuccess = {
                LoadResult.Page(
                    data = it.contents,
                    nextKey = it.cursor,
                    prevKey = null,
                )
            },
            onFailure = {
                LoadResult.Error(it)
            },
        )
    }

    override fun getRefreshKey(state: PagingState<FanboxCursor, FanboxPost>): FanboxCursor? = null
}
