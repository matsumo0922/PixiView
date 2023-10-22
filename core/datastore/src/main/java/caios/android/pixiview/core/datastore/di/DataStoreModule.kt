package caios.android.pixiview.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import caios.android.pixiview.core.common.di.ApplicationScope
import caios.android.pixiview.core.datastore.UserPreference
import caios.android.pixiview.core.datastore.serializer.UserPreferenceSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun providesUserPreferencesDataStore(
        @ApplicationContext context: Context,
        @ApplicationScope scope: CoroutineScope,
        userPreferenceSerializer: UserPreferenceSerializer,
    ): DataStore<UserPreference> {
        return DataStoreFactory.create(
            serializer = userPreferenceSerializer,
            scope = scope,
            produceFile = { context.dataStoreFile("user_preference.pb") },
        )
    }
}
