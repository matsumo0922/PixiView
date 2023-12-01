package caios.android.fanbox.core.billing.usecase

import caios.android.fanbox.core.billing.BillingClient
import caios.android.fanbox.core.billing.ConsumeResult
import com.android.billingclient.api.Purchase
import javax.inject.Inject

class ConsumePlusUseCase @Inject constructor(
    private val billingClient: BillingClient,
) {
    suspend fun execute(purchase: Purchase): ConsumeResult {
        return billingClient.consumePurchase(purchase)
    }
}
