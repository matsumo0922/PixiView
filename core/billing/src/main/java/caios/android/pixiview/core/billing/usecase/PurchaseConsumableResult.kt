package caios.android.pixiview.core.billing.usecase

import caios.android.pixiview.core.billing.PurchaseSingleCommand
import caios.android.pixiview.core.billing.models.ProductDetails
import com.android.billingclient.api.Purchase

data class PurchaseConsumableResult(
    val command: PurchaseSingleCommand,
    val productDetails: ProductDetails,
    val purchase: Purchase,
)
