package caios.android.fanbox.core.datastore

import androidx.datastore.core.DataStore
import caios.android.fanbox.core.model.ThemeColorConfig
import caios.android.fanbox.core.model.ThemeConfig
import caios.android.fanbox.core.model.UserData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PixiViewPreferencesDataStore(
    private val userPreference: DataStore<UserPreference>,
    private val ioDispatcher: CoroutineDispatcher,
) {
    val userData = userPreference.data
        .map {
            UserData(
                pixiViewId = it.pixiviewId,
                themeConfig = when (it.themeConfig) {
                    ThemeConfigProto.THEME_CONFIG_LIGHT -> ThemeConfig.Light
                    ThemeConfigProto.THEME_CONFIG_DARK -> ThemeConfig.Dark
                    else -> ThemeConfig.System
                },
                themeColorConfig = when (it.themeColorConfig) {
                    ThemeColorConfigProto.THEME_COLOR_CONFIG_BLUE -> ThemeColorConfig.Blue
                    ThemeColorConfigProto.THEME_COLOR_CONFIG_BROWN -> ThemeColorConfig.Brown
                    ThemeColorConfigProto.THEME_COLOR_CONFIG_GREEN -> ThemeColorConfig.Green
                    ThemeColorConfigProto.THEME_COLOR_CONFIG_PURPLE -> ThemeColorConfig.Purple
                    ThemeColorConfigProto.THEME_COLOR_CONFIG_PINK -> ThemeColorConfig.Pink
                    else -> ThemeColorConfig.Blue
                },
                isDynamicColor = if (it.hasIsUseDynamicColor()) it.isUseDynamicColor else false,
                isDeveloperMode = if (it.hasIsDeveloperMode()) it.isDeveloperMode else false,
                isAppLock = if (it.hasIsAppLock()) it.isAppLock else false,
                isFollowTabDefaultHome = if (it.hasIsFollowTabDefaultHome()) it.isFollowTabDefaultHome else false,
                isHideAdultContents = if (it.hasIsHideAdultContents()) it.isHideAdultContents else false,
                isPlusMode = if (it.hasIsPlusMode()) it.isPlusMode else false,
                isAgreedPrivacyPolicy = if (it.hasIsAgreedPrivacyPolicy()) it.isAgreedPrivacyPolicy else false,
                isAgreedTermsOfService = if (it.hasIsAgreedTermsOfService()) it.isAgreedTermsOfService else false,
            )
        }

    @Inject
    constructor(
        userPreference: DataStore<UserPreference>,
    ) : this(
        userPreference = userPreference,
        ioDispatcher = Dispatchers.IO,
    )

    suspend fun setPixiViewId(id: String) = withContext(ioDispatcher) {
        userPreference.updateData {
            it.copy {
                this.pixiviewId = id
            }
        }
    }

    suspend fun setAgreedPrivacyPolicy(isAgreed: Boolean) = withContext(ioDispatcher) {
        userPreference.updateData {
            it.copy {
                this.isAgreedPrivacyPolicy = isAgreed
            }
        }
    }

    suspend fun setAgreedTermsOfService(isAgreed: Boolean) = withContext(ioDispatcher) {
        userPreference.updateData {
            it.copy {
                this.isAgreedTermsOfService = isAgreed
            }
        }
    }

    suspend fun setThemeConfig(themeConfig: ThemeConfig) = withContext(ioDispatcher) {
        userPreference.updateData {
            it.copy {
                this.themeConfig = when (themeConfig) {
                    ThemeConfig.Light -> ThemeConfigProto.THEME_CONFIG_LIGHT
                    ThemeConfig.Dark -> ThemeConfigProto.THEME_CONFIG_DARK
                    else -> ThemeConfigProto.THEME_CONFIG_SYSTEM
                }
            }
        }
    }

    suspend fun setThemeColorConfig(themeColorConfig: ThemeColorConfig) = withContext(ioDispatcher) {
        userPreference.updateData {
            it.copy {
                this.themeColorConfig = when (themeColorConfig) {
                    ThemeColorConfig.Blue -> ThemeColorConfigProto.THEME_COLOR_CONFIG_BLUE
                    ThemeColorConfig.Brown -> ThemeColorConfigProto.THEME_COLOR_CONFIG_BROWN
                    ThemeColorConfig.Green -> ThemeColorConfigProto.THEME_COLOR_CONFIG_GREEN
                    ThemeColorConfig.Pink -> ThemeColorConfigProto.THEME_COLOR_CONFIG_PINK
                    ThemeColorConfig.Purple -> ThemeColorConfigProto.THEME_COLOR_CONFIG_PURPLE
                    ThemeColorConfig.Default -> ThemeColorConfigProto.THEME_COLOR_CONFIG_DEFAULT
                    else -> ThemeColorConfigProto.THEME_COLOR_CONFIG_BLUE
                }
            }
        }
    }

    suspend fun setUseDynamicColor(useDynamicColor: Boolean) = withContext(ioDispatcher) {
        userPreference.updateData {
            it.copy {
                this.isUseDynamicColor = useDynamicColor
            }
        }
    }

    suspend fun setAppLock(isAppLock: Boolean) = withContext(ioDispatcher) {
        userPreference.updateData {
            it.copy {
                this.isAppLock = isAppLock
            }
        }
    }

    suspend fun setFollowTabDefaultHome(isFollowTabDefaultHome: Boolean) = withContext(ioDispatcher) {
        userPreference.updateData {
            it.copy {
                this.isFollowTabDefaultHome = isFollowTabDefaultHome
            }
        }
    }

    suspend fun setHideAdultContents(isHideAdultContents: Boolean) = withContext(ioDispatcher) {
        userPreference.updateData {
            it.copy {
                this.isHideAdultContents = isHideAdultContents
            }
        }
    }

    suspend fun setDeveloperMode(isDeveloperMode: Boolean) = withContext(ioDispatcher) {
        userPreference.updateData {
            it.copy {
                this.isDeveloperMode = isDeveloperMode
            }
        }
    }

    suspend fun setPlusMode(isPlusMode: Boolean) = withContext(ioDispatcher) {
        userPreference.updateData {
            it.copy {
                this.isPlusMode = isPlusMode
            }
        }
    }
}
