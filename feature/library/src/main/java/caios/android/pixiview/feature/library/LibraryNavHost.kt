package caios.android.pixiview.feature.library

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import caios.android.pixiview.feature.library.home.LibraryHomeRoute
import caios.android.pixiview.feature.library.home.libraryHomeScreen
import caios.android.pixiview.feature.library.message.libraryMessageScreen
import caios.android.pixiview.feature.library.notify.libraryNotifyScreen

@Composable
fun LibraryNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = LibraryHomeRoute,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        libraryHomeScreen()

        libraryNotifyScreen()

        libraryMessageScreen()
    }
}
