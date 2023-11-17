package caios.android.pixiview.feature.creator.top.items

import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.text.util.LinkifyCompat

@Composable
internal fun CreatorTopDescriptionDialog(
    description: String,
    onDismissRequest: () -> Unit,
) {
    val descriptionFontSize = MaterialTheme.typography.bodyMedium.fontSize
    val descriptionFontColor = MaterialTheme.colorScheme.onSurface

    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surface)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 8.dp),
        ) {
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 16.dp),
                factory = { context ->
                    TextView(context).apply {
                        movementMethod = LinkMovementMethod.getInstance()
                        text = description
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
