package caios.android.pixiview.feature.welcome.login.activity

import android.webkit.WebResourceRequest
import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import caios.android.pixiview.core.common.util.ToastUtil
import caios.android.pixiview.feature.welcome.R
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LoginScreen(
    viewModel: LoginViewModel,
    onDismiss: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val webViewState = rememberWebViewState(viewModel.authCode.url)
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.background(MaterialTheme.colorScheme.surface),
        topBar = {
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
                }
            )
        }
    ) { padding ->
        WebView(
            modifier = Modifier.padding(padding),
            state = webViewState,
            onCreated = { it.settings.javaScriptEnabled = true },
            client = object : AccompanistWebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val url = request?.url
                    val scheme = url?.scheme
                    val code = url?.getQueryParameter("code")

                    if (scheme == "pixiv" && code != null) {
                        scope.launch {
                            viewModel.initAccount(viewModel.authCode.copy(code = code)).fold(
                                onSuccess = { onDismiss.invoke(true) },
                                onFailure = { onDismiss.invoke(false) }
                            )
                        }
                    }

                    return !isAllowedDomain(url.toString())
                }
            }
        )
    }
}

private fun isAllowedDomain(url: String): Boolean {
    Timber.d("isAllowedDomain: $url")

    val allowDomain = listOf(
        "https://accounts.pixiv.net",
        "https://app-api.pixiv.net",
        "https://oauth.secure.pixiv.net",
    )

    return allowDomain.any { url.startsWith(it) }
}
