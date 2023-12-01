package caios.android.fanbox.core.billing.usecase

import android.app.Activity
import caios.android.fanbox.core.billing.BillingClient
import caios.android.fanbox.core.billing.ConsumeResult
import caios.android.fanbox.core.billing.models.ProductDetails
import caios.android.fanbox.core.billing.models.ProductItem
import caios.android.fanbox.core.billing.models.ProductType
import caios.android.fanbox.core.billing.purchaseSingle
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PurchaseDonateUseCase(
    private val billingClient: BillingClient,
    private val mainDispatcher: CoroutineDispatcher,
) {
    @Inject
    constructor(billingClient: BillingClient) : this(
        billingClient = billingClient,
        mainDispatcher = Dispatchers.Main,
    )

    suspend fun execute(activity: Activity, productType: ProductType): PurchaseConsumableResult {
        val productDetails = billingClient.queryProductDetails(ProductItem.plus, productType)
        val purchaseResult = purchase(activity, productDetails)

        // TODO: verification purchaseResult

        consume(purchaseResult)

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

    private suspend fun consume(
        purchaseConsumableResult: PurchaseConsumableResult,
    ): ConsumeResult {
        return billingClient.consumePurchase(purchaseConsumableResult.purchase)
    }
}
