package caios.android.fanbox.core.datastore

import android.content.Context
import caios.android.fanbox.core.model.pixiv.PixivUserAccount
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject

class PreferencePixivAccount @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val accountFile get() = File(context.filesDir, FILE_NAME)

    fun save(account: PixivUserAccount) {
        Json.encodeToString(PixivUserAccount.serializer(), account).also {
            accountFile.writeText(it)
        }
    }

    fun remove() {
        accountFile.delete()
    }

    fun get(): PixivUserAccount? {
        if (!accountFile.exists()) return null

        return accountFile.readText().let {
            Json.decodeFromString(PixivUserAccount.serializer(), it)
        }
    }

    fun getAccountFilePath(): String {
        return accountFile.absolutePath
    }

    companion object {
        private const val FILE_NAME = "PixivUserAccount.json"
    }
}
