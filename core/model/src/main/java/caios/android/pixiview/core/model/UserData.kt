package caios.android.pixiview.core.model

import androidx.compose.runtime.Stable

@Stable
data class UserData(
    val pixiViewId: String,
    val themeConfig: ThemeConfig,
    val themeColorConfig: ThemeColorConfig,
    val isAgreedPrivacyPolicy: Boolean,
    val isAgreedTermsOfService: Boolean,
    val isFollowTabDefaultHome: Boolean,
    val isHideAdultContents: Boolean,
    val isDynamicColor: Boolean,
    val isDeveloperMode: Boolean,
    val isPlusMode: Boolean,
) {
    val hasPrivilege get() = isPlusMode || isDeveloperMode

    companion object {
        fun dummy(): UserData {
            return UserData(
                pixiViewId = "",
                themeConfig = ThemeConfig.System,
                themeColorConfig = ThemeColorConfig.Default,
                isAgreedPrivacyPolicy = false,
                isAgreedTermsOfService = false,
                isFollowTabDefaultHome = false,
                isHideAdultContents = false,
                isDynamicColor = true,
                isDeveloperMode = true,
                isPlusMode = false,
            )
        }
    }
}
