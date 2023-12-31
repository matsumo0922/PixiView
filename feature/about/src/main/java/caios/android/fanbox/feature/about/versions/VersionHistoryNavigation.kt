package caios.android.fanbox.feature.about.versions

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import caios.android.fanbox.core.ui.extensition.navigateWithLog
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet

const val VersionHistoryRoute = "versionHistory"

fun NavController.navigateToVersionHistory() {
    this.navigateWithLog("versionHistory")
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.versionHistoryBottomSheet(
    terminate: () -> Unit,
) {
    bottomSheet(
        route = VersionHistoryRoute,
    ) {
        VersionHistoryRoute(
            modifier = Modifier.fillMaxSize(),
            terminate = terminate,
        )
    }
}
