package caios.android.fanbox.feature.welcome.login.activity

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.CookieManager
import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import caios.android.fanbox.core.ui.component.PixiViewTopBar
import caios.android.fanbox.feature.welcome.R
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.LoadingState
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
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
            PixiViewTopBar(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.welcome_login_title),
                onClickNavigation = { onDismiss.invoke(false) },
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            WebView(
                modifier = Modifier.fillMaxSize(),
                state = webViewState,
                onCreated = {
                    cookieManager.acceptCookie()
                    cookieManager.acceptThirdPartyCookies(it)

                    it.settings.loadWithOverviewMode = true
                    it.settings.domStorageEnabled = true
                    it.settings.javaScriptEnabled = true
                    it.settings.javaScriptCanOpenWindowsAutomatically = true
                },
                client = object : AccompanistWebViewClient() {
                    override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                        if (url == fanboxRedirectUrl) {
                            onUpdateCookie.invoke(cookieManager.getCookie(url))
                            onDismiss.invoke(true)
                        }

                        super.onPageStarted(view, url, favicon)
                    }
                },
            )

            (webViewState.loadingState as? LoadingState.Loading)?.also {
                LinearProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth(),
                    progress = it.progress,
                )
            }
        }
    }
}
