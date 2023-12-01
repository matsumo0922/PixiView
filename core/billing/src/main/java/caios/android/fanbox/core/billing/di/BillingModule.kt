package caios.android.fanbox.core.billing.di

import caios.android.fanbox.core.billing.BillingClient
import caios.android.fanbox.core.billing.BillingClientImpl
import caios.android.fanbox.core.billing.BillingClientProvider
import caios.android.fanbox.core.billing.BillingClientProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface BillingModule {

    @Singleton
    @Binds
    fun bindBillingClientProvider(billingClientProvider: BillingClientProviderImpl): BillingClientProvider

    @Singleton
    @Binds
    fun bindBillingClient(billingClient: BillingClientImpl): BillingClient
}
