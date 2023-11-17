package caios.android.pixiview.core.billing.usecase

import caios.android.pixiview.core.billing.BillingClient
import caios.android.pixiview.core.billing.models.ProductItem
import caios.android.pixiview.core.billing.models.ProductType
import com.android.billingclient.api.Purchase
import javax.inject.Inject

class VerifyPlusUseCase @Inject constructor(
    private val billingClient: BillingClient,
) {
    suspend fun execute(): Purchase? {
        billingClient.queryPurchaseHistory(ProductType.INAPP)

        val productDetails = billingClient.queryProductDetails(ProductItem.plus, ProductType.INAPP)
        val purchases = billingClient.queryPurchases(ProductType.INAPP)

        return purchases.find { it.products.contains(productDetails.productId.toString()) }
    }
}
