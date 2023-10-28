package caios.android.pixiview.feature.library.home.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import caios.android.pixiview.core.common.util.suspendRunCatching
import caios.android.pixiview.core.model.fanbox.FanboxCursor
import caios.android.pixiview.core.model.fanbox.FanboxPost
import caios.android.pixiview.core.repository.FanboxRepository

class LibraryHomePagingSource(
    private val fanboxRepository: FanboxRepository,
) : PagingSource<FanboxCursor, FanboxPost>() {

    override suspend fun load(params: LoadParams<FanboxCursor>): LoadResult<FanboxCursor, FanboxPost> {
        return suspendRunCatching {
            fanboxRepository.getHomePosts(params.key)
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
