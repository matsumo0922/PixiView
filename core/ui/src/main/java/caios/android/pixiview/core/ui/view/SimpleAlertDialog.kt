@file:Suppress("MatchingDeclarationName")

package caios.android.pixiview.core.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import caios.android.pixiview.core.ui.R
import caios.android.pixiview.core.ui.extensition.navigateForResult
import caios.android.pixiview.core.ui.theme.bold

enum class SimpleAlertContents(
    val titleRes: Int,
    val descriptionRes: Int,
    val positiveTextRes: Int? = null,
    val negativeTextRes: Int? = null,
    val isCaution: Boolean = false,
) {
    Logout(
        titleRes = R.string.setting_top_others_logout,
        descriptionRes = R.string.setting_top_others_logout_dialog_description,
        positiveTextRes = R.string.setting_top_others_logout,
        negativeTextRes = R.string.common_cancel,
    ),
    CommentDelete(
        titleRes = R.string.post_detail_comment_delete_title,
        descriptionRes = R.string.post_detail_comment_delete_message,
        positiveTextRes = R.string.common_delete,
        negativeTextRes = R.string.common_cancel,
        isCaution = true,
    ),
}

const val SimpleAlertDialogContent = "simpleAlertDialogSongs"
const val SimpleAlertDialog = "simpleAlertDialog/{$SimpleAlertDialogContent}"

fun NavController.navigateToSimpleAlertDialog(
    content: SimpleAlertContents,
    onClickPositive: (() -> Unit)? = null,
    onClickNegative: (() -> Unit)? = null,
) {
    navigateForResult<Boolean>(
        route = "simpleAlertDialog/$content",
        navResultCallback = {
            if (it) {
                onClickPositive?.invoke()
            } else {
                onClickNegative?.invoke()
            }
        },
    )
}

fun NavGraphBuilder.simpleAlertDialogDialog(
    terminateWithResult: (Boolean) -> Unit,
) {
    dialog(
        route = SimpleAlertDialog,
        arguments = listOf(navArgument(SimpleAlertDialogContent) { type = NavType.StringType }),
    ) { entry ->
        val content = SimpleAlertContents.valueOf(entry.arguments?.getString(SimpleAlertDialogContent).orEmpty())

        SimpleAlertDialog(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(16.dp)),
            title = stringResource(content.titleRes),
            description = stringResource(content.descriptionRes),
            positiveText = content.positiveTextRes?.let { stringResource(it) },
            negativeText = content.negativeTextRes?.let { stringResource(it) },
            onClickPositive = { terminateWithResult.invoke(true) },
            onClickNegative = { terminateWithResult.invoke(false) },
            isCaution = content.isCaution,
        )
    }
}

@Composable
private fun SimpleAlertDialog(
    title: String,
    description: String,
    positiveText: String?,
    negativeText: String?,
    onClickPositive: () -> Unit,
    onClickNegative: () -> Unit,
    isCaution: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = title,
            style = MaterialTheme.typography.titleMedium.bold(),
            color = MaterialTheme.colorScheme.onSurface,
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Row(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (negativeText != null) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(4.dp),
                    onClick = { onClickNegative.invoke() },
                ) {
                    Text(text = negativeText)
                }
            }

            if (positiveText != null) {
                Button(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(if (isCaution) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary),
                    onClick = { onClickPositive.invoke() },
                ) {
                    Text(text = positiveText)
                }
            }
        }
    }
}
