package caios.android.fanbox.feature.creator.top.items

import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.widget.TextView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.util.LinkifyCompat
import caios.android.fanbox.core.model.fanbox.FanboxCreatorDetail

@Composable
internal fun CreatorTopDescriptionSection(
    creatorDetail: FanboxCreatorDetail,
    modifier: Modifier = Modifier,
) {
    val descriptionFontSize = MaterialTheme.typography.bodyMedium.fontSize
    val descriptionFontColor = MaterialTheme.colorScheme.onSurface

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AndroidView(
                modifier = Modifier.padding(horizontal = 16.dp),
                factory = { context ->
                    TextView(context).apply {
                        movementMethod = LinkMovementMethod.getInstance()
                        text = creatorDetail.description
                        textSize = descriptionFontSize.value
                        setTextColor(descriptionFontColor.toArgb())
                    }.also {
                        LinkifyCompat.addLinks(it, Linkify.WEB_URLS)
                    }
                },
            )
        }
    }
}
