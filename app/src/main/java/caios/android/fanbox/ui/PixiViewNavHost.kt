package caios.android.fanbox.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import caios.android.fanbox.core.ui.extensition.PixiViewNavigationType
import caios.android.fanbox.core.ui.extensition.popBackStackWithResult
import caios.android.fanbox.core.ui.view.navigateToSimpleAlertDialog
import caios.android.fanbox.core.ui.view.simpleAlertDialogDialog
import caios.android.fanbox.feature.about.about.aboutScreen
import caios.android.fanbox.feature.about.about.navigateToAbout
import caios.android.fanbox.feature.about.billing.billingPlusBottomSheet
import caios.android.fanbox.feature.about.billing.navigateToBillingPlus
import caios.android.fanbox.feature.about.versions.navigateToVersionHistory
import caios.android.fanbox.feature.about.versions.versionHistoryBottomSheet
import caios.android.fanbox.feature.creator.download.creatorPostsDownloadDialog
import caios.android.fanbox.feature.creator.download.navigateToCreatorPostsDownload
import caios.android.fanbox.feature.creator.fancard.fanCardScreen
import caios.android.fanbox.feature.creator.fancard.navigateToFanCard
import caios.android.fanbox.feature.creator.follow.followingCreatorsScreen
import caios.android.fanbox.feature.creator.follow.navigateToFollowingCreators
import caios.android.fanbox.feature.creator.payment.navigateToPayments
import caios.android.fanbox.feature.creator.payment.paymentsScreen
import caios.android.fanbox.feature.creator.support.navigateToSupportingCreators
import caios.android.fanbox.feature.creator.support.supportingCreatorsScreen
import caios.android.fanbox.feature.creator.top.creatorTopScreen
import caios.android.fanbox.feature.creator.top.navigateToCreatorTop
import caios.android.fanbox.feature.library.LibraryNavHost
import caios.android.fanbox.feature.library.LibraryRoute
import caios.android.fanbox.feature.library.component.LibraryPermanentDrawer
import caios.android.fanbox.feature.library.libraryScreen
import caios.android.fanbox.feature.library.navigateToLibraryDestination
import caios.android.fanbox.feature.post.bookmark.bookmarkedPostsScreen
import caios.android.fanbox.feature.post.bookmark.navigateToBookmarkedPosts
import caios.android.fanbox.feature.post.detail.PostDetailPagingType
import caios.android.fanbox.feature.post.detail.navigateToPostDetail
import caios.android.fanbox.feature.post.detail.postDetailScreen
import caios.android.fanbox.feature.post.image.navigateToPostImage
import caios.android.fanbox.feature.post.image.postImageScreen
import caios.android.fanbox.feature.post.search.navigateToPostSearch
import caios.android.fanbox.feature.post.search.postSearchScreen
import caios.android.fanbox.feature.setting.developer.navigateToSettingDeveloper
import caios.android.fanbox.feature.setting.developer.settingDeveloperDialog
import caios.android.fanbox.feature.setting.oss.navigateToSettingLicense
import caios.android.fanbox.feature.setting.oss.settingLicenseScreen
import caios.android.fanbox.feature.setting.theme.navigateToSettingTheme
import caios.android.fanbox.feature.setting.theme.settingThemeScreen
import caios.android.fanbox.feature.setting.top.navigateToSettingTop
import caios.android.fanbox.feature.setting.top.settingTopScreen
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
internal fun PixiViewNavHost(
    navigationType: PixiViewNavigationType,
    modifier: Modifier = Modifier,
    bottomSheetNavigator: BottomSheetNavigator = rememberBottomSheetNavigator(),
    startDestination: String = LibraryRoute,
) {
    ModalBottomSheetLayout(
        modifier = modifier,
        bottomSheetNavigator = bottomSheetNavigator,
    ) {
        when (navigationType) {
            PixiViewNavigationType.PermanentNavigationDrawer -> {
                ExpandedNavHost(
                    modifier = Modifier.fillMaxSize(),
                    bottomSheetNavigator = bottomSheetNavigator,
                    subNavController = rememberNavController(bottomSheetNavigator),
                )
            }

            PixiViewNavigationType.NavigationRail -> {
                MediumNavHost(
                    modifier = Modifier.fillMaxSize(),
                    bottomSheetNavigator = bottomSheetNavigator,
                    startDestination = startDestination,
                )
            }

            PixiViewNavigationType.BottomNavigation -> {
                CompactNavHost(
                    modifier = Modifier.fillMaxSize(),
                    bottomSheetNavigator = bottomSheetNavigator,
                    startDestination = startDestination,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
private fun CompactNavHost(
    modifier: Modifier = Modifier,
    bottomSheetNavigator: BottomSheetNavigator = rememberBottomSheetNavigator(),
    navController: NavHostController = rememberNavController(bottomSheetNavigator),
    startDestination: String = LibraryRoute,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
    ) {
        applyNavGraph(navController)
    }
}

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
private fun MediumNavHost(
    modifier: Modifier = Modifier,
    bottomSheetNavigator: BottomSheetNavigator = rememberBottomSheetNavigator(),
    navController: NavHostController = rememberNavController(bottomSheetNavigator),
    startDestination: String = LibraryRoute,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
    ) {
        applyNavGraph(navController)
    }
}

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
private fun ExpandedNavHost(
    modifier: Modifier = Modifier,
    bottomSheetNavigator: BottomSheetNavigator = rememberBottomSheetNavigator(),
    mainNavController: NavHostController = rememberNavController(bottomSheetNavigator),
    subNavController: NavHostController = rememberNavController(bottomSheetNavigator),
) {
    PermanentNavigationDrawer(
        modifier = modifier,
        drawerContent = {
            LibraryPermanentDrawer(
                currentDestination = mainNavController.currentBackStackEntryAsState().value?.destination,
                onClickLibrary = mainNavController::navigateToLibraryDestination,
                navigateToBookmarkedPosts = { mainNavController.navigateToBookmarkedPosts() },
                navigateToFollowingCreators = { mainNavController.navigateToFollowingCreators() },
                navigateToSupportingCreators = { mainNavController.navigateToSupportingCreators() },
                navigateToPayments = { subNavController.navigateToPayments() },
                navigateToSetting = { mainNavController.navigateToSettingTop() },
                navigateToAbout = { subNavController.navigateToAbout() },
                navigateToBillingPlus = { mainNavController.navigateToBillingPlus() },
            )
        },
    ) {
        Row(Modifier.fillMaxSize()) {
            LibraryNavHost(
                modifier = Modifier.weight(1f),
                navController = mainNavController,
                openDrawer = { /* do nothing */ },
                navigateToPostSearch = { mainNavController.navigateToPostSearch() },
                navigateToPostDetailFromHome = { subNavController.navigateToPostDetail(it, PostDetailPagingType.Home) },
                navigateToPostDetailFromSupported = { subNavController.navigateToPostDetail(it, PostDetailPagingType.Supported) },
                navigateToCreatorPosts = { mainNavController.navigateToCreatorTop(it, isPosts = true) },
                navigateToCreatorPlans = { mainNavController.navigateToCreatorTop(it) },
            ) {
                applyNavGraph(mainNavController, subNavController)
            }

            NavHost(
                modifier = Modifier.weight(1f),
                navController = subNavController,
                startDestination = EmptyDetailRoute,
            ) {
                applyNavGraph(mainNavController, subNavController)
            }
        }
    }
}

/**
 * mainNavController
 *   - Library*
 *   - CreatorTop
 *   - PostSearch
 *   - BookmarkedPosts
 *   - FollowingCreators
 *   - SupportingCreators
 *   - Setting*
 *   - Dialogs*
 */
private fun NavGraphBuilder.applyNavGraph(
    mainNavController: NavHostController,
    subNavController: NavHostController = mainNavController,
) {
    // composable

    libraryScreen(
        navigateToPostSearch = { mainNavController.navigateToPostSearch() },
        navigateToPostDetailFromHome = { subNavController.navigateToPostDetail(it, PostDetailPagingType.Home) },
        navigateToPostDetailFromSupported = { subNavController.navigateToPostDetail(it, PostDetailPagingType.Supported) },
        navigateToCreatorPosts = { mainNavController.navigateToCreatorTop(it, isPosts = true) },
        navigateToCreatorPlans = { mainNavController.navigateToCreatorTop(it) },
        navigateToBookmarkedPosts = { mainNavController.navigateToBookmarkedPosts() },
        navigateToFollowerCreators = { mainNavController.navigateToFollowingCreators() },
        navigateToSupportingCreators = { mainNavController.navigateToSupportingCreators() },
        navigateToPayments = { subNavController.navigateToPayments() },
        navigateToSettingTop = { subNavController.navigateToSettingTop() },
        navigateToAbout = { subNavController.navigateToAbout() },
        navigateToBillingPlus = { mainNavController.navigateToBillingPlus() },
    )

    postDetailScreen(
        navigateToPostSearch = { query, creatorId -> mainNavController.navigateToPostSearch(tag = query, creatorId = creatorId) },
        navigateToPostDetail = { subNavController.navigateToPostDetail(it, PostDetailPagingType.Unknown) },
        navigateToPostImage = { postId, index -> subNavController.navigateToPostImage(postId, index) },
        navigateToCreatorPosts = { mainNavController.navigateToCreatorTop(it, isPosts = true) },
        navigateToCreatorPlans = { mainNavController.navigateToCreatorTop(it) },
        navigateToCommentDeleteDialog = { contents, onResult -> mainNavController.navigateToSimpleAlertDialog(contents, onResult) },
        terminate = { subNavController.popBackStack() },
    )

    postImageScreen(
        terminate = { subNavController.popBackStack() },
    )

    postSearchScreen(
        navigateToPostSearch = { creatorId, creatorQuery, tag -> mainNavController.navigateToPostSearch(creatorId, creatorQuery, tag) },
        navigateToPostDetail = { subNavController.navigateToPostDetail(it, PostDetailPagingType.Search) },
        navigateToCreatorPosts = { mainNavController.navigateToCreatorTop(it, isPosts = true) },
        navigateToCreatorPlans = { mainNavController.navigateToCreatorTop(it) },
        terminate = { mainNavController.popBackStack() },
    )

    bookmarkedPostsScreen(
        navigateToPostDetail = { subNavController.navigateToPostDetail(it, PostDetailPagingType.Unknown) },
        navigateToCreatorPosts = { mainNavController.navigateToCreatorTop(it, isPosts = true) },
        navigateToCreatorPlans = { mainNavController.navigateToCreatorTop(it) },
        terminate = { mainNavController.popBackStack() },
    )

    creatorTopScreen(
        navigateToPostDetail = { subNavController.navigateToPostDetail(it, PostDetailPagingType.Creator) },
        navigateToPostSearch = { query, creatorId -> mainNavController.navigateToPostSearch(tag = query, creatorId = creatorId) },
        navigateToBillingPlus = { mainNavController.navigateToBillingPlus() },
        navigateToDownloadAll = { mainNavController.navigateToCreatorPostsDownload(it) },
        terminate = { mainNavController.popBackStack() },
    )

    supportingCreatorsScreen(
        navigateToCreatorPlans = { mainNavController.navigateToCreatorTop(it) },
        navigateToCreatorPosts = { mainNavController.navigateToCreatorTop(it, isPosts = true) },
        navigateToFanCard = { subNavController.navigateToFanCard(it) },
        terminate = { mainNavController.popBackStack() },
    )

    followingCreatorsScreen(
        navigateToCreatorPlans = { mainNavController.navigateToCreatorTop(it) },
        terminate = { mainNavController.popBackStack() },
    )

    paymentsScreen(
        navigateToCreatorPosts = { mainNavController.navigateToCreatorTop(it, isPosts = true) },
        terminate = { subNavController.popBackStack() },
    )

    fanCardScreen(
        terminate = { subNavController.popBackStack() },
    )

    aboutScreen(
        navigateToVersionHistory = { mainNavController.navigateToVersionHistory() },
        navigateToDonate = { mainNavController.navigateToBillingPlus() },
        terminate = { subNavController.popBackStack() },
    )

    settingTopScreen(
        navigateToThemeSetting = { mainNavController.navigateToSettingTheme() },
        navigateToBillingPlus = { mainNavController.navigateToBillingPlus() },
        navigateToSettingDeveloper = { mainNavController.navigateToSettingDeveloper() },
        navigateToLogoutDialog = { contents, onResult -> mainNavController.navigateToSimpleAlertDialog(contents, onResult) },
        navigateToOpenSourceLicense = { mainNavController.navigateToSettingLicense() },
        terminate = { mainNavController.popBackStack() },
    )

    settingThemeScreen(
        navigateToBillingPlus = { mainNavController.navigateToBillingPlus() },
        terminate = { mainNavController.popBackStack() },
    )

    settingLicenseScreen(
        terminate = { mainNavController.popBackStack() },
    )

    settingDeveloperDialog(
        terminate = { mainNavController.popBackStack() },
    )

    // dialog

    simpleAlertDialogDialog(
        terminateWithResult = { mainNavController.popBackStackWithResult(it) },
    )

    creatorPostsDownloadDialog(
        terminate = { mainNavController.popBackStack() },
    )

    // bottom sheet

    versionHistoryBottomSheet(
        terminate = { mainNavController.popBackStack() },
    )

    billingPlusBottomSheet(
        terminate = { mainNavController.popBackStack() },
    )

    // empty for start destination

    emptyDetailScreen()
}
