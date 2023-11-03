package caios.android.pixiview.core.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagItems(
    tags: ImmutableList<String>,
    onClickTag: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        for (tag in tags) {
            TagItem(
                tag = tag,
                onClickTag = onClickTag,
                modifier = Modifier.padding(4.dp),
            )
        }
    }
}

@Composable
private fun TagItem(
    tag: String,
    onClickTag: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable { onClickTag(tag) }
            .border(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(4.dp),
            ),
    ) {
        Text(
            modifier = Modifier.padding(8.dp, 4.dp),
            text = tag,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TagItemsPreview() {
    TagItems(
        tags = persistentListOf("たこ焼き", "秋刀魚", "メガネ", "ゲーミングPC", "机", "コーヒー", "Nintendo Switch", "ティッシュ", "食洗機"),
        onClickTag = {},
    )
}
