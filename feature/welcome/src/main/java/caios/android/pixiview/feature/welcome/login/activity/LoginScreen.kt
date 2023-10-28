package caios.android.pixiview.feature.welcome.login.activity

import android.graphics.Bitmap
import android.webkit.CookieManager
import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import caios.android.pixiview.feature.welcome.R
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.LoadingState
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LoginScreen(
    onUpdateCookie: (String) -> Unit,
    onDismiss: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val fanboxUrl = "https://www.fanbox.cc/login"
    val fanboxRedirectUrl = "https://www.fanbox.cc/creators/find"

    val cookieManager = remember { CookieManager.getInstance() }
    val webViewState = rememberWebViewState("$fanboxUrl?return_to=$fanboxRedirectUrl")

    Scaffold(
        modifier = modifier.background(MaterialTheme.colorScheme.surface),
        topBar = {
            Column {
                TopAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    title = {
                        Text(
                            text = stringResource(R.string.welcome_login_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    },
                    navigationIcon = {
                        Icon(
                            modifier = Modifier
                                .size(40.dp)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(50))
                                .clickable { onDismiss.invoke(false) }
                                .padding(8.dp),
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    },
                )

                (webViewState.loadingState as? LoadingState.Loading)?.also {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        progress = { it.progress },
                    )
                }
            }
        },
    ) { padding ->
        WebView(
            modifier = Modifier.padding(padding),
            state = webViewState,
            onCreated = {
                cookieManager.acceptCookie()
                cookieManager.acceptThirdPartyCookies(it)

                it.settings.javaScriptEnabled = true
            },
            client = object : AccompanistWebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    if (url == fanboxRedirectUrl) {
                        onUpdateCookie.invoke(cookieManager.getCookie(url))
                        onDismiss.invoke(true)
                    }

                    super.onPageStarted(view, url, favicon)
                }
            },
        )
    }
}
