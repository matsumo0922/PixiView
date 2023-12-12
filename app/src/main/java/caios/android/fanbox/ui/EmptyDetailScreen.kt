package caios.android.fanbox.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import caios.android.fanbox.R
import caios.android.fanbox.core.ui.view.EmptyView

const val EmptyDetailRoute = "Empty"

fun NavGraphBuilder.emptyDetailScreen() {
    composable(EmptyDetailRoute) {
        EmptyView(
            modifier = Modifier.fillMaxSize(),
            titleRes = R.string.empty_detail_title,
            messageRes = R.string.empty_detail_description,
        )
    }
}
