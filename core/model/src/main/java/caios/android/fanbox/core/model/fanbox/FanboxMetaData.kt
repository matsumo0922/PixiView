package caios.android.fanbox.core.model.fanbox

import caios.android.fanbox.core.model.fanbox.id.CreatorId

data class FanboxMetaData(
    val apiUrl: String,
    val context: Context,
    val csrfToken: String,
    val invitationsDisabled: Boolean,
    val isOnCc: Boolean,
    val urlContext: UrlContext,
    val wwwUrl: String,
) {
    data class Context(
        val privacyPolicy: PrivacyPolicy,
        val user: User,
    ) {
        data class PrivacyPolicy(
            val policyUrl: String,
            val revisionHistoryUrl: String,
            val shouldShowNotice: Boolean,
            val updateDate: String,
        )

        data class User(
            val creatorId: CreatorId?,
            val fanboxUserStatus: Int,
            val hasAdultContent: Boolean,
            val hasUnpaidPayments: Boolean,
            val iconUrl: String?,
            val isCreator: Boolean,
            val isMailAddressOutdated: Boolean,
            val isSupporter: Boolean,
            val lang: String,
            val name: String,
            val planCount: Int,
            val showAdultContent: Boolean,
            val userId: String?,
        ) {
            companion object {
                fun User.asFanboxUser() = FanboxUser(
                    iconUrl = iconUrl.orEmpty(),
                    name = name,
                    userId = userId.orEmpty(),
                    creatorId = CreatorId(creatorId?.value.orEmpty()),
                )
            }
        }
    }

    data class UrlContext(
        val creatorOriginPattern: String,
        val rootOriginPattern: String,
    )

    companion object {
        fun dummy() = FanboxMetaData(
            apiUrl = "",
            context = Context(
                privacyPolicy = Context.PrivacyPolicy(
                    policyUrl = "",
                    revisionHistoryUrl = "",
                    shouldShowNotice = false,
                    updateDate = "",
                ),
                user = Context.User(
                    creatorId = null,
                    fanboxUserStatus = 0,
                    hasAdultContent = false,
                    hasUnpaidPayments = false,
                    iconUrl = null,
                    isCreator = false,
                    isMailAddressOutdated = false,
                    isSupporter = false,
                    lang = "ja",
                    name = "Unknown",
                    planCount = 0,
                    showAdultContent = false,
                    userId = "0",
                ),
            ),
            csrfToken = "",
            invitationsDisabled = false,
            isOnCc = false,
            urlContext = UrlContext(
                creatorOriginPattern = "",
                rootOriginPattern = "",
            ),
            wwwUrl = "",
        )
    }
}
