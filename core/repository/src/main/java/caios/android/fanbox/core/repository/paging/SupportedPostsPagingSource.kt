package caios.android.fanbox.core.repository.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import caios.android.fanbox.core.common.util.suspendRunCatching
import caios.android.fanbox.core.model.fanbox.FanboxCursor
import caios.android.fanbox.core.model.fanbox.FanboxPost
import caios.android.fanbox.core.repository.FanboxRepository

class SupportedPostsPagingSource(
    private val fanboxRepository: FanboxRepository,
    private val isHideRestricted: Boolean,
) : PagingSource<FanboxCursor, FanboxPost>() {

    override suspend fun load(params: LoadParams<FanboxCursor>): LoadResult<FanboxCursor, FanboxPost> {
        return suspendRunCatching {
            fanboxRepository.getSupportedPosts(params.key, params.loadSize)
        }.fold(
            onSuccess = { page ->
                LoadResult.Page(
                    data = if (isHideRestricted) page.contents.filter { !it.isRestricted } else page.contents,
                    nextKey = page.cursor,
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
