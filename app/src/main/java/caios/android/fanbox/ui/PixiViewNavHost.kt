package caios.android.fanbox.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
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
import caios.android.fanbox.feature.library.LibraryRoute
import caios.android.fanbox.feature.library.libraryScreen
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
    modifier: Modifier = Modifier,
    bottomSheetNavigator: BottomSheetNavigator = rememberBottomSheetNavigator(),
    navController: NavHostController = rememberNavController(bottomSheetNavigator),
    startDestination: String = LibraryRoute,
) {
    ModalBottomSheetLayout(
        modifier = modifier,
        bottomSheetNavigator = bottomSheetNavigator,
    ) {
        NavHost(
            modifier = Modifier.fillMaxSize(),
            navController = navController,
            startDestination = startDestination,
        ) {
            // composable

            libraryScreen(
                navigateToPostSearch = { navController.navigateToPostSearch() },
                navigateToPostDetailFromHome = { navController.navigateToPostDetail(it, PostDetailPagingType.Home) },
                navigateToPostDetailFromSupported = { navController.navigateToPostDetail(it, PostDetailPagingType.Supported) },
                navigateToCreatorPosts = { navController.navigateToCreatorTop(it, isPosts = true) },
                navigateToCreatorPlans = { navController.navigateToCreatorTop(it) },
                navigateToBookmarkedPosts = { navController.navigateToBookmarkedPosts() },
                navigateToFollowerCreators = { navController.navigateToFollowingCreators() },
                navigateToSupportingCreators = { navController.navigateToSupportingCreators() },
                navigateToPayments = { navController.navigateToPayments() },
                navigateToSettingTop = { navController.navigateToSettingTop() },
                navigateToAbout = { navController.navigateToAbout() },
                navigateToBillingPlus = { navController.navigateToBillingPlus() },
            )

            postDetailScreen(
                navigateToPostSearch = { query, creatorId -> navController.navigateToPostSearch(tag = query, creatorId = creatorId) },
                navigateToPostDetail = { navController.navigateToPostDetail(it, PostDetailPagingType.Unknown) },
                navigateToPostImage = { postId, index -> navController.navigateToPostImage(postId, index) },
                navigateToCreatorPosts = { navController.navigateToCreatorTop(it, isPosts = true) },
                navigateToCreatorPlans = { navController.navigateToCreatorTop(it) },
                navigateToCommentDeleteDialog = { contents, onResult -> navController.navigateToSimpleAlertDialog(contents, onResult) },
                terminate = { navController.popBackStack() },
            )

            postImageScreen(
                terminate = { navController.popBackStack() },
            )

            postSearchScreen(
                navigateToPostSearch = { creatorId, creatorQuery, tag -> navController.navigateToPostSearch(creatorId, creatorQuery, tag) },
                navigateToPostDetail = { navController.navigateToPostDetail(it, PostDetailPagingType.Search) },
                navigateToCreatorPosts = { navController.navigateToCreatorTop(it, isPosts = true) },
                navigateToCreatorPlans = { navController.navigateToCreatorTop(it) },
                terminate = { navController.popBackStack() },
            )

            bookmarkedPostsScreen(
                navigateToPostDetail = { navController.navigateToPostDetail(it, PostDetailPagingType.Unknown) },
                navigateToCreatorPosts = { navController.navigateToCreatorTop(it, isPosts = true) },
                navigateToCreatorPlans = { navController.navigateToCreatorTop(it) },
                terminate = { navController.popBackStack() },
            )

            creatorTopScreen(
                navigateToPostDetail = { navController.navigateToPostDetail(it, PostDetailPagingType.Creator) },
                navigateToPostSearch = { query, creatorId -> navController.navigateToPostSearch(tag = query, creatorId = creatorId) },
                navigateToBillingPlus = { navController.navigateToBillingPlus() },
                navigateToDownloadAll = { navController.navigateToCreatorPostsDownload(it) },
                terminate = { navController.popBackStack() },
            )

            supportingCreatorsScreen(
                navigateToCreatorPlans = { navController.navigateToCreatorTop(it) },
                navigateToCreatorPosts = { navController.navigateToCreatorTop(it, isPosts = true) },
                navigateToFanCard = { navController.navigateToFanCard(it) },
                terminate = { navController.popBackStack() },
            )

            followingCreatorsScreen(
                navigateToCreatorPlans = { navController.navigateToCreatorTop(it) },
                terminate = { navController.popBackStack() },
            )

            paymentsScreen(
                navigateToCreatorPosts = { navController.navigateToCreatorTop(it, isPosts = true) },
                terminate = { navController.popBackStack() },
            )

            fanCardScreen(
                terminate = { navController.popBackStack() },
            )

            aboutScreen(
                navigateToVersionHistory = { navController.navigateToVersionHistory() },
                navigateToDonate = { navController.navigateToBillingPlus() },
                terminate = { navController.popBackStack() },
            )

            settingTopScreen(
                navigateToThemeSetting = { navController.navigateToSettingTheme() },
                navigateToBillingPlus = { navController.navigateToBillingPlus() },
                navigateToSettingDeveloper = { navController.navigateToSettingDeveloper() },
                navigateToLogoutDialog = { contents, onResult -> navController.navigateToSimpleAlertDialog(contents, onResult) },
                navigateToOpenSourceLicense = { navController.navigateToSettingLicense() },
                terminate = { navController.popBackStack() },
            )

            settingThemeScreen(
                navigateToBillingPlus = { navController.navigateToBillingPlus() },
                terminate = { navController.popBackStack() },
            )

            settingLicenseScreen(
                terminate = { navController.popBackStack() },
            )

            settingDeveloperDialog(
                terminate = { navController.popBackStack() },
            )

            // dialog

            simpleAlertDialogDialog(
                terminateWithResult = { navController.popBackStackWithResult(it) },
            )

            creatorPostsDownloadDialog(
                terminate = { navController.popBackStack() },
            )

            // bottom sheet

            versionHistoryBottomSheet(
                terminate = { navController.popBackStack() },
            )

            billingPlusBottomSheet(
                terminate = { navController.popBackStack() },
            )
        }
    }
}
