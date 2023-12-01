@file:Suppress("MatchingDeclarationName")

package caios.android.fanbox.core.ui.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PixiViewNavigationBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    NavigationBar(
        modifier = modifier,
        contentColor = PixiViewNavigationDefaults.navigationContentColor(),
        tonalElevation = 0.dp,
        content = content,
    )
}

@Composable
fun RowScope.PixiViewNavigationBarItem(
    icon: @Composable () -> Unit,
    label: @Composable () -> Unit,
    onClick: () -> Unit,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    NavigationBarItem(
        modifier = modifier,
        selected = isSelected,
        onClick = onClick,
        icon = icon,
        label = label,
        alwaysShowLabel = false,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = PixiViewNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = PixiViewNavigationDefaults.navigationContentColor(),
            selectedTextColor = PixiViewNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = PixiViewNavigationDefaults.navigationContentColor(),
            indicatorColor = PixiViewNavigationDefaults.navigationIndicatorColor(),
        ),
    )
}

object PixiViewNavigationDefaults {
    @Composable
    fun navigationContentColor() = MaterialTheme.colorScheme.onSurfaceVariant

    @Composable
    fun navigationSelectedItemColor() = MaterialTheme.colorScheme.onPrimaryContainer

    @Composable
    fun navigationIndicatorColor() = MaterialTheme.colorScheme.primaryContainer
}
