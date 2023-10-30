package caios.android.pixiview.core.model.fanbox.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FanboxCreatorPlanEntity(
    @SerialName("body")
    val body: Body,
) {
    @Serializable
    data class Body(
        @SerialName("plan")
        val plan: Plan,
        @SerialName("supportStartDatetime")
        val supportStartDatetime: String,
        @SerialName("supportTransactions")
        val supportTransactions: List<SupportTransaction>,
        @SerialName("supporterCardImageUrl")
        val supporterCardImageUrl: String,
    ) {
        @Serializable
        data class Plan(
            @SerialName("coverImageUrl")
            val coverImageUrl: String?,
            @SerialName("creatorId")
            val creatorId: String,
            @SerialName("description")
            val description: String,
            @SerialName("fee")
            val fee: Int,
            @SerialName("hasAdultContent")
            val hasAdultContent: Boolean,
            @SerialName("id")
            val id: String,
            @SerialName("paymentMethod")
            val paymentMethod: String,
            @SerialName("title")
            val title: String,
            @SerialName("user")
            val user: User,
        ) {
            @Serializable
            data class User(
                @SerialName("iconUrl")
                val iconUrl: String?,
                @SerialName("name")
                val name: String,
                @SerialName("userId")
                val userId: String,
            )
        }

        @Serializable
        data class SupportTransaction(
            @SerialName("id")
            val id: String,
            @SerialName("paidAmount")
            val paidAmount: Int,
            @SerialName("supporter")
            val supporter: Supporter,
            @SerialName("targetMonth")
            val targetMonth: String,
            @SerialName("transactionDatetime")
            val transactionDatetime: String,
        ) {
            @Serializable
            data class Supporter(
                @SerialName("iconUrl")
                val iconUrl: String?,
                @SerialName("name")
                val name: String,
                @SerialName("userId")
                val userId: String,
            )
        }
    }
}
