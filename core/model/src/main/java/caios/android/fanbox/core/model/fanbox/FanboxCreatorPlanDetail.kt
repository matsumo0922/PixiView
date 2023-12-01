package caios.android.fanbox.core.model.fanbox

import java.time.OffsetDateTime

data class FanboxCreatorPlanDetail(
    val plan: FanboxCreatorPlan,
    val supportStartDatetime: String,
    val supportTransactions: List<SupportTransaction>,
    val supporterCardImageUrl: String,
) {
    data class SupportTransaction(
        val id: String,
        val paidAmount: Int,
        val transactionDatetime: OffsetDateTime,
        val targetMonth: String,
        val user: FanboxUser,
    )

    companion object {
        fun dummy() = FanboxCreatorPlanDetail(
            plan = FanboxCreatorPlan.dummy(),
            supportStartDatetime = "",
            supportTransactions = listOf(
                SupportTransaction(
                    id = "",
                    paidAmount = 0,
                    transactionDatetime = OffsetDateTime.now(),
                    targetMonth = "",
                    user = FanboxUser.dummy(),
                ),
            ),
            supporterCardImageUrl = "",
        )
    }
}
