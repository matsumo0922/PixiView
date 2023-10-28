package caios.android.pixiview.core.repository

import caios.android.pixiview.core.datastore.PreferenceFanboxCookie
import caios.android.pixiview.core.model.PageInfo
import caios.android.pixiview.core.model.fanbox.FanboxCursor
import caios.android.pixiview.core.model.fanbox.FanboxPost
import caios.android.pixiview.core.model.fanbox.entity.FanboxPostItemsEntity
import caios.android.pixiview.core.repository.utils.parse
import caios.android.pixiview.core.repository.utils.translate
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

interface FanboxRepository {
    val cookie: SharedFlow<String>

    fun hasActiveCookie(): Boolean

    suspend fun updateCookie(cookie: String)

    suspend fun getHomePosts(cursor: FanboxCursor? = null): PageInfo<FanboxPost>?
}

class FanboxRepositoryImpl @Inject constructor(
    private val client: HttpClient,
    private val fanboxCookiePreference: PreferenceFanboxCookie,
): FanboxRepository {

    override val cookie: SharedFlow<String> = fanboxCookiePreference.data

    override fun hasActiveCookie(): Boolean {
        return fanboxCookiePreference.get() != null
    }

    override suspend fun updateCookie(cookie: String) {
        fanboxCookiePreference.save(cookie)
    }

    override suspend fun getHomePosts(cursor: FanboxCursor?): PageInfo<FanboxPost>? {
        return get("post.listHome", mapOf("limit" to "10")).parse<FanboxPostItemsEntity>()?.translate()
    }

    private suspend fun get(dir: String, parameters: Map<String, String>): HttpResponse {
        return client.get {
            url("$API/$dir")

            for ((key, value) in parameters) {
                parameter(key, value)
            }

            header("origin", "https://www.fanbox.cc")
            header("referer", "https://www.fanbox.cc")
            header("user-agent",  "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36")
            header("Cookie", cookie.first())
        }
    }

    companion object {
        private const val API = "https://api.fanbox.cc"
    }
}
