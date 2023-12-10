package caios.android.fanbox.core.repository

import caios.android.fanbox.core.datastore.PixiViewPreferencesDataStore
import caios.android.fanbox.core.model.ThemeColorConfig
import caios.android.fanbox.core.model.ThemeConfig
import caios.android.fanbox.core.model.UserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface UserDataRepository {

    val userData: Flow<UserData>

    suspend fun setPixiViewId(id: String)
    suspend fun setAgreedPrivacyPolicy(isAgreed: Boolean)
    suspend fun setAgreedTermsOfService(isAgreed: Boolean)
    suspend fun setThemeConfig(themeConfig: ThemeConfig)
    suspend fun setThemeColorConfig(themeColorConfig: ThemeColorConfig)
    suspend fun setAppLock(isAppLock: Boolean)
    suspend fun setFollowTabDefaultHome(isFollowTabDefaultHome: Boolean)
    suspend fun setHideAdultContents(isHideAdultContents: Boolean)
    suspend fun setOverrideAdultContents(isOverrideAdultContents: Boolean)
    suspend fun setTestUser(isTestUser: Boolean)
    suspend fun setHideRestricted(isHideRestricted: Boolean)
    suspend fun setGridMode(isGridMode: Boolean)
    suspend fun setDeveloperMode(isDeveloperMode: Boolean)
    suspend fun setPlusMode(isPlusMode: Boolean)
    suspend fun setUseDynamicColor(useDynamicColor: Boolean)
}

class UserDataRepositoryImpl @Inject constructor(
    private val pixiViewPreferencesDataStore: PixiViewPreferencesDataStore,
) : UserDataRepository {

    override val userData: Flow<UserData> = pixiViewPreferencesDataStore.userData

    override suspend fun setPixiViewId(id: String) {
        pixiViewPreferencesDataStore.setPixiViewId(id)
    }

    override suspend fun setAgreedPrivacyPolicy(isAgreed: Boolean) {
        pixiViewPreferencesDataStore.setAgreedPrivacyPolicy(isAgreed)
    }

    override suspend fun setAgreedTermsOfService(isAgreed: Boolean) {
        pixiViewPreferencesDataStore.setAgreedTermsOfService(isAgreed)
    }

    override suspend fun setThemeConfig(themeConfig: ThemeConfig) {
        pixiViewPreferencesDataStore.setThemeConfig(themeConfig)
    }

    override suspend fun setThemeColorConfig(themeColorConfig: ThemeColorConfig) {
        pixiViewPreferencesDataStore.setThemeColorConfig(themeColorConfig)
    }

    override suspend fun setAppLock(isAppLock: Boolean) {
        pixiViewPreferencesDataStore.setAppLock(isAppLock)
    }

    override suspend fun setFollowTabDefaultHome(isFollowTabDefaultHome: Boolean) {
        pixiViewPreferencesDataStore.setFollowTabDefaultHome(isFollowTabDefaultHome)
    }

    override suspend fun setHideAdultContents(isHideAdultContents: Boolean) {
        pixiViewPreferencesDataStore.setHideAdultContents(isHideAdultContents)
    }

    override suspend fun setOverrideAdultContents(isOverrideAdultContents: Boolean) {
        pixiViewPreferencesDataStore.setOverrideAdultContents(isOverrideAdultContents)
    }

    override suspend fun setTestUser(isTestUser: Boolean) {
        pixiViewPreferencesDataStore.setTestUser(isTestUser)
    }

    override suspend fun setHideRestricted(isHideRestricted: Boolean) {
        pixiViewPreferencesDataStore.setHideRestricted(isHideRestricted)
    }

    override suspend fun setGridMode(isGridMode: Boolean) {
        pixiViewPreferencesDataStore.setGridMode(isGridMode)
    }

    override suspend fun setDeveloperMode(isDeveloperMode: Boolean) {
        pixiViewPreferencesDataStore.setDeveloperMode(isDeveloperMode)
    }

    override suspend fun setPlusMode(isPlusMode: Boolean) {
        pixiViewPreferencesDataStore.setPlusMode(isPlusMode)
    }

    override suspend fun setUseDynamicColor(useDynamicColor: Boolean) {
        pixiViewPreferencesDataStore.setUseDynamicColor(useDynamicColor)
    }
}
