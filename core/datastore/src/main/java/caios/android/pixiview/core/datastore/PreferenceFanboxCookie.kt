package caios.android.pixiview.core.datastore

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import timber.log.Timber
import javax.inject.Inject

class PreferenceFanboxCookie @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val preference by lazy { context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE) }

    private val _data = MutableSharedFlow<String>(replay = 1)

    val data: SharedFlow<String> = _data.asSharedFlow()

    fun save(cookie: String) {
        Timber.d("save cookie: $cookie")

        preference.edit {
            putString(KEY_COOKIE, cookie)
        }

        _data.tryEmit(cookie)
    }

    fun get(): String? {
        return preference.getString(KEY_COOKIE, null)
    }

    companion object {
        private const val PREFERENCE = "FanboxCookie"
        private const val KEY_COOKIE = "cookie"
    }
}
