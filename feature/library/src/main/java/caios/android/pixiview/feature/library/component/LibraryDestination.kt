package caios.android.pixiview.feature.library.component

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import caios.android.pixiview.feature.library.R

internal enum class LibraryDestination(
    val selectedIcon: ImageVector,
    val deselectedIcon: ImageVector,
    @StringRes val title: Int,
) {
    Home(
        selectedIcon = Icons.Default.Home,
        deselectedIcon = Icons.Outlined.Home,
        title = R.string.library_navigation_home,
    ),
    Discovery(
        selectedIcon = Icons.Default.Search,
        deselectedIcon = Icons.Outlined.Search,
        title = R.string.library_navigation_discovery,
    ),
    Notify(
        selectedIcon = Icons.Default.Notifications,
        deselectedIcon = Icons.Outlined.Notifications,
        title = R.string.library_navigation_notify,
    ),
    Message(
        selectedIcon = Icons.Default.Mail,
        deselectedIcon = Icons.Outlined.Mail,
        title = R.string.library_navigation_message,
    ),
}

internal fun NavDestination?.isLibraryDestinationInHierarchy(destination: LibraryDestination): Boolean {
    return this?.hierarchy?.any { it.route?.contains(destination.name, true) ?: false } == true
}
