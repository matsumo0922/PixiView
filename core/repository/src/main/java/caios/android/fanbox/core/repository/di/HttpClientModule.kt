package caios.android.fanbox.core.repository.di

import android.webkit.CookieManager
import caios.android.fanbox.core.datastore.PreferenceFanboxCookie
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.Cookie
import io.ktor.http.Url
import io.ktor.http.parseServerSetCookieHeader
import io.ktor.http.renderSetCookieHeader
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.net.UnknownHostException
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HttpClientModule {

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun provideHttpClient(preference: PreferenceFanboxCookie, formatter: Json): HttpClient {
        val cookieManager = CookieManager.getInstance()

        return HttpClient(OkHttp) {
            install(Logging) {
                level = LogLevel.INFO
                logger = object : Logger {
                    override fun log(message: String) {
                        Timber.d(message)
                    }
                }
            }

            install(HttpCookies) {
                storage = object : CookiesStorage {
                    override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {
                        cookieManager.setCookie(requestUrl.toString(), renderSetCookieHeader(cookie))
                    }

                    override suspend fun get(requestUrl: Url): List<Cookie> {
                        val url = requestUrl.toString()
                        val cookiesString = cookieManager.getCookie(url)

                        if (!cookiesString.isNullOrBlank()) {
                            val cookieHeaders = cookiesString.split(";")
                            val cookies = mutableListOf<Cookie?>()

                            for (header in cookieHeaders) {
                                cookies.add(parseServerSetCookieHeader(header))
                            }

                            preference.save(cookiesString)

                            return cookies.filterNotNull()
                        }

                        return emptyList()
                    }

                    override fun close() {
                        /* do nothing */
                    }
                }
            }

            install(ContentNegotiation) {
                json(formatter)
            }

            install(HttpRequestRetry) {
                retryOnExceptionIf(maxRetries = 3) { _, throwable -> throwable is UnknownHostException }
                exponentialDelay()
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun provideFormatter(): Json {
        return Json {
            isLenient = true
            prettyPrint = true
            ignoreUnknownKeys = true
            coerceInputValues = true
            encodeDefaults = true
            explicitNulls = false
        }
    }
}
