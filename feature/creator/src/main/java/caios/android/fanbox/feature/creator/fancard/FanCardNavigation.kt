package caios.android.fanbox.feature.creator.fancard

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import caios.android.fanbox.core.model.fanbox.id.CreatorId
import caios.android.fanbox.core.ui.animation.NavigateAnimation

const val FanCardId = "fanCardId"
const val FanCardRoute = "fanCard/{$FanCardId}"

fun NavController.navigateToFanCard(creatorId: CreatorId) {
    this.navigate("fanCard/$creatorId")
}

fun NavGraphBuilder.fanCardScreen(
    terminate: () -> Unit,
) {
    composable(
        route = FanCardRoute,
        arguments = listOf(navArgument(FanCardId) { type = NavType.StringType }),
        enterTransition = { NavigateAnimation.Horizontal.enter },
        exitTransition = { NavigateAnimation.Horizontal.exit },
        popEnterTransition = { NavigateAnimation.Horizontal.popEnter },
        popExitTransition = { NavigateAnimation.Horizontal.popExit },
    ) {
        FanCardRoute(
            modifier = Modifier.fillMaxSize(),
            creatorId = CreatorId(it.arguments?.getString(FanCardId) ?: ""),
            terminate = terminate,
        )
    }
}
