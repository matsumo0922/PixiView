package caios.android.fanbox.feature.about.billing

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DesignServices
import androidx.compose.material.icons.filled.DoNotDisturb
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.HideImage
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import caios.android.fanbox.core.ui.AsyncLoadContents
import caios.android.fanbox.core.ui.theme.bold
import caios.android.fanbox.feature.about.R
import com.android.billingclient.api.Purchase
import kotlinx.coroutines.launch

@Composable
internal fun BillingPlusRoute(
    terminate: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BillingPlusViewModel = hiltViewModel(),
) {
    val activity = LocalContext.current as Activity
    val scope = rememberCoroutineScope()
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()

    BackHandler {
        terminate.invoke()
    }

    AsyncLoadContents(
        modifier = modifier,
        screenState = screenState,
        retryAction = { terminate.invoke() },
    ) { uiState ->
        BillingPlusDialog(
            modifier = Modifier.fillMaxSize(),
            purchase = uiState.purchase,
            formattedPrice = uiState.formattedPrice,
            isDeveloperMode = uiState.isDeveloperMode,
            onClickPurchase = {
                scope.launch {
                    if (viewModel.purchase(activity)) {
                        terminate.invoke()
                    }
                }
            },
            onClickVerify = {
                scope.launch {
                    if (viewModel.verify(activity)) {
                        terminate.invoke()
                    }
                }
            },
            onClickConsume = {
                scope.launch {
                    viewModel.consume(activity, it)
                }
            },
        )
    }
}

@Composable
private fun BillingPlusDialog(
    purchase: Purchase?,
    formattedPrice: String?,
    isDeveloperMode: Boolean,
    onClickPurchase: () -> Unit,
    onClickVerify: () -> Unit,
    onClickConsume: (Purchase) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .statusBarsPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TitleItem(
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth(),
        )

        Text(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            text = stringResource(R.string.billing_plus_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Button(
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth(),
            onClick = { onClickPurchase.invoke() },
        ) {
            Text(stringResource(R.string.billing_plus_purchase_button, formattedPrice ?: "￥300"))
        }

        OutlinedButton(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            onClick = { onClickVerify.invoke() },
        ) {
            Text(stringResource(R.string.billing_plus_verify_button))
        }

        if (purchase != null && isDeveloperMode) {
            OutlinedButton(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                onClick = { onClickConsume.invoke(purchase) },
            ) {
                Text(stringResource(R.string.billing_plus_consume_button))
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(top = 24.dp)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth(),
                    text = stringResource(R.string.billing_plus_caution1),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Text(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth(),
                    text = stringResource(R.string.billing_plus_caution2),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(8.dp))

                PlusItem(
                    modifier = Modifier.fillMaxWidth(),
                    title = R.string.billing_plus_item_hide_ads,
                    description = R.string.billing_plus_item_hide_ads_description,
                    icon = Icons.Default.DoNotDisturb,
                )

                PlusItem(
                    modifier = Modifier.fillMaxWidth(),
                    title = R.string.billing_plus_item_download,
                    description = R.string.billing_plus_item_download_description,
                    icon = Icons.Default.Download,
                )

                PlusItem(
                    modifier = Modifier.fillMaxWidth(),
                    title = R.string.billing_plus_item_lock,
                    description = R.string.billing_plus_item_lock_description,
                    icon = Icons.Default.Lock,
                )

                PlusItem(
                    modifier = Modifier.fillMaxWidth(),
                    title = R.string.billing_plus_item_hide_restricted,
                    description = R.string.billing_plus_item_hide_restricted_description,
                    icon = Icons.Outlined.HideImage,
                )

                PlusItem(
                    modifier = Modifier.fillMaxWidth(),
                    title = R.string.billing_plus_item_widget,
                    description = R.string.billing_plus_item_widget_description,
                    icon = Icons.Default.Widgets,
                )

                PlusItem(
                    modifier = Modifier.fillMaxWidth(),
                    title = R.string.billing_plus_item_material_you,
                    description = R.string.billing_plus_item_material_you_description,
                    icon = Icons.Default.DesignServices,
                )

                PlusItem(
                    modifier = Modifier.fillMaxWidth(),
                    title = R.string.billing_plus_item_accent_color,
                    description = R.string.billing_plus_item_accent_color_description,
                    icon = Icons.Default.ColorLens,
                )

                PlusItem(
                    modifier = Modifier.fillMaxWidth(),
                    title = R.string.billing_plus_item_feature,
                    description = R.string.billing_plus_item_feature_description,
                    icon = Icons.Default.MoreHoriz,
                )

                PlusItem(
                    modifier = Modifier.fillMaxWidth(),
                    title = R.string.billing_plus_item_support,
                    description = R.string.billing_plus_item_support_description,
                    icon = Icons.Outlined.HelpOutline,
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surface,
                                Color.Transparent,
                            ),
                        ),
                    ),
            )
        }
    }
}

@Composable
private fun TitleItem(modifier: Modifier = Modifier) {
    val titleStyle = MaterialTheme.typography.headlineLarge.bold()
    val annotatedString = buildAnnotatedString {
        append("Buy ")

        withStyle(titleStyle.copy(color = MaterialTheme.colorScheme.primary).toSpanStyle()) {
            append("FANBOX Viewer+")
        }
    }

    Text(
        modifier = modifier,
        text = annotatedString,
        style = titleStyle,
        color = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
private fun PlusItem(
    @StringRes title: Int,
    @StringRes description: Int,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = icon,
            contentDescription = null,
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(description),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview
@Composable
private fun BillingPlusScreenPreview() {
    BillingPlusDialog(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxSize(),
        purchase = null,
        formattedPrice = null,
        isDeveloperMode = true,
        onClickPurchase = {},
        onClickVerify = {},
        onClickConsume = {},
    )
}
