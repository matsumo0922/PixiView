package caios.android.pixiview.core.repository

import caios.android.pixiview.core.model.PageInfo
import caios.android.pixiview.core.model.fanbox.FanboxCursor
import caios.android.pixiview.core.model.fanbox.FanboxPost
import caios.android.pixiview.core.model.fanbox.entity.FanboxPostItemsEntity
import caios.android.pixiview.core.repository.utils.parse
import caios.android.pixiview.core.repository.utils.translate
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Cookie
import io.ktor.http.HttpHeaders
import io.ktor.http.Parameters
import io.ktor.http.decodeCookieValue
import io.ktor.http.parseServerSetCookieHeader
import io.ktor.http.renderCookieHeader
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.internal.parseCookie
import timber.log.Timber
import javax.inject.Inject

interface FanboxRepository {
    val cookie: SharedFlow<String>

    suspend fun updateCookie(cookie: String)

    suspend fun getHomePosts(cursor: FanboxCursor? = null): PageInfo<FanboxPost>?
}

class FanboxRepositoryImpl @Inject constructor(
    private val client: HttpClient,
): FanboxRepository {

    private val _cookie = MutableSharedFlow<String>(replay = 1)

    override val cookie: SharedFlow<String> = _cookie.asSharedFlow()

    override suspend fun updateCookie(cookie: String) {
        Timber.d("updateCookie: $cookie")
        _cookie.emit(cookie)
    }

    override suspend fun getHomePosts(cursor: FanboxCursor?): PageInfo<FanboxPost>? {
        val cookie = cookie.first()
        val a = client.submitForm(
            url = "$API/post.listHome",
            formParameters = Parameters.build {
                append("limit", 10.toString())

                cursor?.also {
                    append("maxPublishedDatetime", it.maxPublishedDatetime)
                    append("maxId", it.maxId)
                }
            }
        ) {
            headers["Cookie"] = cookie
        }

        val b = a.bodyAsText()

        Timber.d("getHomePosts: $b")

        return a.parse<FanboxPostItemsEntity>()?.translate()
    }

    private suspend fun HttpRequestBuilder.applyCookie()  {
        /*val ktorCookie = parseServerSetCookieHeader(cookie.first())
        val renderedCookie = renderCookieHeader(ktorCookie)

        if (HttpHeaders.Cookie !in headers) {
            headers.append(HttpHeaders.Cookie, renderedCookie)
            return
        }
        // Client cookies are stored in a single header "Cookies" and multiple values are separated with ";"
        headers[HttpHeaders.Cookie] = headers[HttpHeaders.Cookie] + "; " + renderedCookie*/

        val c = cookie.first()

        Timber.d("applyCookie: $c")

        headers[HttpHeaders.Cookie] = c
    }

    companion object {
        private const val API = "https://api.fanbox.cc"
    }
}
