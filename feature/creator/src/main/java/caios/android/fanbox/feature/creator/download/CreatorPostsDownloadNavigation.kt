package caios.android.fanbox.feature.creator.download

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import caios.android.fanbox.core.model.fanbox.id.CreatorId

const val CreatorPostsDownloadId = "creatorPostsDownloadId"
const val CreatorPostsDownloadRoute = "creatorPostsDownload/{$CreatorPostsDownloadId}"

fun NavController.navigateToCreatorPostsDownload(creatorId: CreatorId) {
    this.navigate("creatorPostsDownload/$creatorId")
}

fun NavGraphBuilder.creatorPostsDownloadDialog(
    terminate: () -> Unit,
) {
    dialog(
        route = CreatorPostsDownloadRoute,
        arguments = listOf(navArgument(CreatorPostsDownloadId) { type = NavType.StringType }),
    ) {
        CreatorPostsDownloadRoute(
            modifier = Modifier.fillMaxWidth(),
            creatorId = CreatorId(it.arguments?.getString(CreatorPostsDownloadId) ?: ""),
            terminate = terminate,
        )
    }
}
