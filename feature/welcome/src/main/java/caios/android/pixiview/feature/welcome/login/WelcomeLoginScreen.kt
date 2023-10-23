package caios.android.pixiview.feature.welcome.login

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import caios.android.kanade.feature.welcome.WelcomeIndicatorItem
import caios.android.pixiview.core.common.util.ToastUtil
import caios.android.pixiview.core.ui.theme.bold
import caios.android.pixiview.core.ui.theme.center
import caios.android.pixiview.feature.welcome.R
import caios.android.pixiview.feature.welcome.login.activity.LoginActivity
import kotlinx.coroutines.launch

@Composable
internal fun WelcomeLoginScreen(
    navigateToWelcomePermission: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WelcomeLoginViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    var isLoggedIn by remember { mutableStateOf(viewModel.isLoggedIn()) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            ToastUtil.show(context, R.string.welcome_login_toast_success)
            isLoggedIn = true
        } else {
            ToastUtil.show(context, R.string.welcome_login_toast_failed)
        }
    }

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            modifier = Modifier.padding(
                top = 8.dp,
                start = 8.dp,
                end = 8.dp,
            ),
            painter = painterResource(R.drawable.vec_welcome_plus),
            contentDescription = null,
        )

        Text(
            modifier = Modifier.padding(top = 32.dp),
            text = stringResource(if (isLoggedIn) R.string.welcome_login_ready_title else R.string.welcome_login_title),
            style = MaterialTheme.typography.displaySmall.bold(),
            color = MaterialTheme.colorScheme.primary,
        )

        Text(
            modifier = Modifier.padding(top = 12.dp),
            text = stringResource(if (isLoggedIn) R.string.welcome_login_ready_message else R.string.welcome_login_message),
            style = MaterialTheme.typography.bodySmall.center(),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.weight(1f))

        WelcomeIndicatorItem(
            modifier = Modifier.padding(bottom = 24.dp),
            max = 3,
            step = 2,
        )

        if (isLoggedIn) {
            Button(
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(50),
                onClick = { navigateToWelcomePermission.invoke() },
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = stringResource(R.string.welcome_login_button_next),
                )
            }
        } else {
            Button(
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(50),
                onClick = { launcher.launch(Intent(context, LoginActivity::class.java)) },
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = stringResource(R.string.welcome_login_button_login),
                )
            }
        }
    }
}
