package caios.android.pixiview.core.ui.extensition

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import coil.request.ImageRequest
import com.google.accompanist.placeholder.PlaceholderDefaults
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.shimmerHighlightColor
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.shimmer

@Immutable
data class FanboxCookie(
    val cookie: String = ""
)

val LocalFanboxCookie = staticCompositionLocalOf { FanboxCookie() }

@Composable
fun ImageRequest.Builder.fanboxHeader(): ImageRequest.Builder {
    val cookie = LocalFanboxCookie.current.cookie

    addHeader("origin", "https://www.fanbox.cc")
    addHeader("referer", "https://www.fanbox.cc")
    addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36")

    if (cookie.isNotBlank()) {
        addHeader("Cookie", cookie)
    }

    return this
}

@Composable
fun SimmerPlaceHolder(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .placeholder(
                color = MaterialTheme.colorScheme.surfaceVariant,
                visible = true,
                highlight = PlaceholderHighlight.shimmer(
                    highlightColor = PlaceholderDefaults.shimmerHighlightColor(
                        backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                ),
            )
    )
}
