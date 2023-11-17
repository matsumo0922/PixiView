package caios.android.pixiview.core.model.fanbox

import caios.android.pixiview.core.model.fanbox.id.CreatorId

data class FanboxMetaData(
    val apiUrl: String,
    val context: Context,
    val csrfToken: String,
    val invitationsDisabled: Boolean,
    val isOnCc: Boolean,
    val urlContext: UrlContext,
    val wwwUrl: String
) {
    data class Context(
        val privacyPolicy: PrivacyPolicy,
        val user: User
    ) {
        data class PrivacyPolicy(
            val policyUrl: String,
            val revisionHistoryUrl: String,
            val shouldShowNotice: Boolean,
            val updateDate: String
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
            val userId: String,
        )
    }

    data class UrlContext(
        val creatorOriginPattern: String,
        val rootOriginPattern: String,
    )
}
