package caios.android.fanbox.core.billing.usecase

import android.app.Activity
import caios.android.fanbox.core.billing.AcknowledgeResult
import caios.android.fanbox.core.billing.BillingClient
import caios.android.fanbox.core.billing.models.ProductDetails
import caios.android.fanbox.core.billing.models.ProductItem
import caios.android.fanbox.core.billing.models.ProductType
import caios.android.fanbox.core.billing.purchaseSingle
import com.android.billingclient.api.Purchase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PurchasePlusSubscriptionUseCase(
    private val billingClient: BillingClient,
    private val mainDispatcher: CoroutineDispatcher,
) {
    @Inject
    constructor(billingClient: BillingClient) : this(
        billingClient = billingClient,
        mainDispatcher = Dispatchers.Main,
    )

    suspend fun execute(activity: Activity): PurchaseConsumableResult {
        val productDetails = billingClient.queryProductDetails(ProductItem.plusSubscription, ProductType.SUBS)
        val purchaseResult = purchase(activity, productDetails)

        acknowledge(purchaseResult.purchase)

        return purchaseResult
    }

    private suspend fun purchase(
        activity: Activity,
        productDetails: ProductDetails,
    ): PurchaseConsumableResult = withContext(mainDispatcher) {
        val command = purchaseSingle(productDetails, null)
        val result = billingClient.launchBillingFlow(activity, command)

        PurchaseConsumableResult(command, productDetails, result.billingPurchase)
    }

    private suspend fun acknowledge(purchase: Purchase): AcknowledgeResult {
        return billingClient.acknowledgePurchase(purchase)
    }
}
