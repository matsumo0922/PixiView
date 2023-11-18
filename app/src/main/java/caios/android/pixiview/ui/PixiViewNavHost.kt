package caios.android.pixiview.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import caios.android.pixiview.feature.about.about.aboutScreen
import caios.android.pixiview.feature.about.about.navigateToAbout
import caios.android.pixiview.feature.about.billing.billingPlusBottomSheet
import caios.android.pixiview.feature.about.billing.navigateToBillingPlus
import caios.android.pixiview.feature.about.versions.navigateToVersionHistory
import caios.android.pixiview.feature.about.versions.versionHistoryBottomSheet
import caios.android.pixiview.feature.creator.fancard.fanCardScreen
import caios.android.pixiview.feature.creator.fancard.navigateToFanCard
import caios.android.pixiview.feature.creator.follow.followingCreatorsScreen
import caios.android.pixiview.feature.creator.follow.navigateToFollowingCreators
import caios.android.pixiview.feature.creator.support.navigateToSupportingCreators
import caios.android.pixiview.feature.creator.support.supportingCreatorsScreen
import caios.android.pixiview.feature.creator.top.creatorTopScreen
import caios.android.pixiview.feature.creator.top.navigateToCreatorTop
import caios.android.pixiview.feature.library.LibraryRoute
import caios.android.pixiview.feature.library.libraryScreen
import caios.android.pixiview.feature.post.detail.navigateToPostDetail
import caios.android.pixiview.feature.post.detail.postDetailScreen
import caios.android.pixiview.feature.post.image.navigateToPostImage
import caios.android.pixiview.feature.post.image.postImageScreen
import caios.android.pixiview.feature.post.liked.likedPostsScreen
import caios.android.pixiview.feature.post.liked.navigateToLikedPosts
import caios.android.pixiview.feature.post.search.navigateToPostSearch
import caios.android.pixiview.feature.post.search.postSearchScreen
import caios.android.pixiview.feature.setting.developer.navigateToSettingDeveloper
import caios.android.pixiview.feature.setting.developer.settingDeveloperDialog
import caios.android.pixiview.feature.setting.oss.navigateToSettingLicense
import caios.android.pixiview.feature.setting.oss.settingLicenseScreen
import caios.android.pixiview.feature.setting.theme.navigateToSettingTheme
import caios.android.pixiview.feature.setting.theme.settingThemeScreen
import caios.android.pixiview.feature.setting.top.navigateToSettingTop
import caios.android.pixiview.feature.setting.top.settingTopScreen
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
                navigateToPostDetail = { navController.navigateToPostDetail(it) },
                navigateToCreatorPosts = { navController.navigateToCreatorTop(it, isPosts = true) },
                navigateToCreatorPlans = { navController.navigateToCreatorTop(it) },
                navigateToLikedPosts = { navController.navigateToLikedPosts() },
                navigateToFollowerCreators = { navController.navigateToFollowingCreators() },
                navigateToSupportingCreators = { navController.navigateToSupportingCreators() },
                navigateToSettingTop = { navController.navigateToSettingTop() },
                navigateToAbout = { navController.navigateToAbout() },
                navigateToBillingPlus = { navController.navigateToBillingPlus() },
            )

            postDetailScreen(
                navigateToPostDetail = { navController.navigateToPostDetail(it) },
                navigateToPostImage = { postId, index -> navController.navigateToPostImage(postId, index) },
                navigateToCreatorPosts = { navController.navigateToCreatorTop(it, isPosts = true) },
                navigateToCreatorPlans = { navController.navigateToCreatorTop(it) },
                terminate = { navController.popBackStack() },
            )

            postImageScreen(
                terminate = { navController.popBackStack() },
            )

            postSearchScreen(
                navigateToPostDetail = { navController.navigateToPostDetail(it) },
                navigateToCreatorPosts = { navController.navigateToCreatorTop(it, isPosts = true) },
                navigateToCreatorPlans = { navController.navigateToCreatorTop(it) },
                terminate = { navController.popBackStack() },
            )

            likedPostsScreen(
                navigateToPostDetail = { navController.navigateToPostDetail(it) },
                navigateToCreatorPosts = { navController.navigateToCreatorTop(it, isPosts = true) },
                navigateToCreatorPlans = { navController.navigateToCreatorTop(it) },
                terminate = { navController.popBackStack() },
            )

            creatorTopScreen(
                navigateToPostDetail = { navController.navigateToPostDetail(it) },
                navigateToPostSearch = { query, creatorId -> navController.navigateToPostSearch(tag = query, creatorId = creatorId) },
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

            fanCardScreen(
                terminate = { navController.popBackStack() },
            )

            aboutScreen(
                navigateToVersionHistory = { navController.navigateToVersionHistory() },
                navigateToDonate = { navController.navigateToBillingPlus() },
                terminate = { navController.popBackStack() },
            )

            settingTopScreen(
                navigateToSettingTheme = { navController.navigateToSettingTheme() },
                navigateToSettingDeveloper = { navController.navigateToSettingDeveloper() },
                navigateToOpenSourceLicense = { navController.navigateToSettingLicense() },
                terminate = { navController.popBackStack() },
            )

            settingThemeScreen(
                navigateToBillingPlus = { },
                terminate = { navController.popBackStack() },
            )

            settingLicenseScreen(
                terminate = { navController.popBackStack() },
            )

            settingDeveloperDialog(
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
