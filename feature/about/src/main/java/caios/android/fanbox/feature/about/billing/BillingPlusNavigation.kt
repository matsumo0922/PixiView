package caios.android.fanbox.feature.about.billing

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import caios.android.fanbox.core.ui.extensition.navigateWithLog
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet

const val BillingPlusRoute = "billingPlus"

fun NavController.navigateToBillingPlus() {
    this.navigateWithLog(BillingPlusRoute)
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.billingPlusBottomSheet(
    terminate: () -> Unit,
) {
    bottomSheet(BillingPlusRoute) {
        BillingPlusRoute(
            modifier = Modifier.fillMaxSize(),
            terminate = terminate,
        )
    }
}
