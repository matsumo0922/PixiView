package caios.android.fanbox.core.billing.usecase

import caios.android.fanbox.core.billing.BillingClient
import caios.android.fanbox.core.billing.models.ProductItem
import caios.android.fanbox.core.billing.models.ProductType
import com.android.billingclient.api.Purchase
import javax.inject.Inject

class VerifyPlusUseCase @Inject constructor(
    private val billingClient: BillingClient,
) {
    suspend fun execute(): Purchase? {
        return verifyInAppPurchase() ?: verifySubscriptionPurchase()
    }

    private suspend fun verifyInAppPurchase(): Purchase? {
        billingClient.queryPurchaseHistory(ProductType.INAPP)

        val productDetails = billingClient.queryProductDetails(ProductItem.plus, ProductType.INAPP)
        val purchases = billingClient.queryPurchases(ProductType.INAPP)

        return purchases.find { it.products.contains(productDetails.productId.toString()) }
    }

    private suspend fun verifySubscriptionPurchase(): Purchase? {
        billingClient.queryPurchaseHistory(ProductType.SUBS)

        val productDetails = billingClient.queryProductDetails(ProductItem.plusSubscription, ProductType.SUBS)
        val purchases = billingClient.queryPurchases(ProductType.SUBS)

        return purchases.find { it.products.contains(productDetails.productId.toString()) }
    }
}
