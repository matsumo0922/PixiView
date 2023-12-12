package caios.android.fanbox.feature.welcome.login

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import caios.android.fanbox.core.common.util.ToastUtil
import caios.android.fanbox.core.ui.extensition.LocalNavigationType
import caios.android.fanbox.core.ui.extensition.PixiViewNavigationType
import caios.android.fanbox.core.ui.theme.bold
import caios.android.fanbox.core.ui.theme.center
import caios.android.fanbox.feature.welcome.R
import caios.android.fanbox.feature.welcome.WelcomeIndicatorItem
import caios.android.fanbox.feature.welcome.login.activity.LoginActivity

@Composable
internal fun WelcomeLoginScreen(
    navigateToWelcomePermission: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WelcomeLoginViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val navigationType = LocalNavigationType.current.type

    val isLoggedIn by viewModel.isLoggedInFlow.collectAsStateWithLifecycle(initialValue = false)
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            ToastUtil.show(context, R.string.welcome_login_toast_success)
            viewModel.fetchLoggedIn()
        } else {
            ToastUtil.show(context, R.string.welcome_login_toast_failed)
        }
    }

    if (navigationType != PixiViewNavigationType.PermanentNavigationDrawer) {
        Column(
            modifier = modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            FirstSection()

            SecondSection(
                modifier = Modifier.weight(1f),
                isLoggedIn = isLoggedIn,
                launcher = launcher,
                navigateToWelcomePermission = navigateToWelcomePermission,
            )
        }
    } else {
        Row(
            modifier = modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FirstSection(
                modifier = Modifier.weight(1f),
            )

            Spacer(modifier = Modifier.width(24.dp))

            Column(Modifier.weight(1f)) {
                Box(Modifier.weight(2f))

                SecondSection(
                    modifier = Modifier.weight(3f),
                    isLoggedIn = isLoggedIn,
                    launcher = launcher,
                    navigateToWelcomePermission = navigateToWelcomePermission,
                )
            }
        }
    }
}

@Composable
private fun FirstSection(
    modifier: Modifier = Modifier,
) {
    Image(
        modifier = modifier.padding(
            top = 8.dp,
            start = 8.dp,
            end = 8.dp,
        ),
        painter = painterResource(R.drawable.vec_welcome_plus),
        contentDescription = null,
    )
}

@Composable
private fun SecondSection(
    isLoggedIn: Boolean,
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    navigateToWelcomePermission: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
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
                shape = CircleShape,
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
                shape = CircleShape,
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
