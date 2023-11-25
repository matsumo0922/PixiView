package caios.android.pixiview.feature.post.search.items

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import caios.android.pixiview.core.model.FanboxTag
import caios.android.pixiview.core.ui.theme.bold
import caios.android.pixiview.feature.post.R
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun PostSearchSuggestTagsSection(
    suggestTags: ImmutableList<FanboxTag>,
    onClickTag: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isShowAllTag by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier.animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        for (item in if (isShowAllTag) suggestTags else suggestTags.take(3)) {
            TagItem(
                modifier = Modifier.fillMaxWidth(),
                tag = item,
                onClickTag = onClickTag,
            )
        }

        if (!isShowAllTag && suggestTags.size > 3) {
            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = { isShowAllTag = true },
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = null,
                    )

                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = stringResource(R.string.common_see_more),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        }
    }
}

@Composable
private fun TagItem(
    tag: FanboxTag,
    onClickTag: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable { onClickTag.invoke("#${tag.name}") },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = "#${tag.name}",
                style = MaterialTheme.typography.titleMedium.bold(),
                color = MaterialTheme.colorScheme.onSurface,
            )

            Card(
                shape = RoundedCornerShape(4.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer),
            ) {
                Text(
                    modifier = Modifier.padding(6.dp, 4.dp),
                    text = stringResource(R.string.unit_tag, tag.count),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
