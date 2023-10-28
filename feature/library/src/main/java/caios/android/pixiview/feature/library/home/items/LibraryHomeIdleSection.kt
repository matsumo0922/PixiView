package caios.android.pixiview.feature.library.home.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import caios.android.pixiview.core.model.fanbox.FanboxPost
import coil.compose.AsyncImage

@Composable
internal fun LibraryHomeIdleSection(
    pagingAdapter: LazyPagingItems<FanboxPost>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier) {
        items(
            count = pagingAdapter.itemCount,
            key = pagingAdapter.itemKey { it.id },
            contentType = pagingAdapter.itemContentType(),
        ) { index ->
            pagingAdapter[index]?.let {
                Card(
                    modifier = Modifier.padding(16.dp, 8.dp)
                ) {
                    Column {
                        AsyncImage(
                            modifier = Modifier
                                .fillMaxSize()
                                .aspectRatio(16 / 9f),
                            model = it.cover?.url,
                            contentDescription = null,
                        )

                        Text(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            text = it.title,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
            }
        }
    }
}
