package caios.android.pixiview.core.repository

import caios.android.pixiview.core.common.PixiViewConfig
import caios.android.pixiview.core.datastore.PreferencePixivAccount
import caios.android.pixiview.core.model.pixiv.PixivAuthCode
import caios.android.pixiview.core.model.pixiv.PixivUserAccount
import caios.android.pixiview.core.repository.utils.parse
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.submitForm
import io.ktor.http.Parameters
import io.ktor.http.formUrlEncode
import java.security.MessageDigest
import java.time.LocalDateTime
import java.util.Base64
import javax.inject.Inject

interface PixivRepository {
    suspend fun initAccount(code: PixivAuthCode): PixivUserAccount?
    suspend fun refreshAccount(refreshToken: String): PixivUserAccount?

    fun hasActiveAccount(): Boolean
    fun getAuthCode(): PixivAuthCode

    suspend fun getActiveAccount(): PixivUserAccount?
}

class PixivRepositoryImpl @Inject constructor(
    private val client: HttpClient,
    private val config: PixiViewConfig,
    private val userAccountPreference: PreferencePixivAccount,
): PixivRepository {

    override suspend fun initAccount(code: PixivAuthCode): PixivUserAccount? {
        return client.submitForm(
            url = "${AUTH_ENDPOINT}/auth/token",
            formParameters = Parameters.build {
                append("client_id", config.pixivClientId)
                append("client_secret", config.pixivClientSecret)
                append("code", code.code)
                append("code_verifier", code.codeVerifier)
                append("grant_type", "authorization_code")
                append("include_policy", true.toString())
                append("redirect_uri", "${API_ENDPOINT}/web/v1/users/auth/pixiv/callback")
            }
        ).parse<PixivUserAccount?>()?.also {
            userAccountPreference.save(it)
        }
    }

    override suspend fun refreshAccount(refreshToken: String): PixivUserAccount? {
        return client.submitForm(
            url = "${AUTH_ENDPOINT}/auth/token",
            formParameters = Parameters.build {
                append("client_id", config.pixivClientId)
                append("client_secret", config.pixivClientSecret)
                append("include_policy", true.toString())
                append("grant_type", "refresh_token")
                append("refresh_token", refreshToken)
            }
        ).parse<PixivUserAccount?>()?.also {
            userAccountPreference.save(it)
        }
    }

    override fun hasActiveAccount(): Boolean {
        return userAccountPreference.get() != null
    }

    override fun getAuthCode(): PixivAuthCode {
        val codeVerifier = getCodeVerifier(32)
        val codeChallenge = getCodeChallenge(codeVerifier)
        val params = listOf(
            "code_challenge" to codeChallenge,
            "code_challenge_method" to "S256",
            "client" to "pixiv-android"
        )

        return PixivAuthCode(
            code = "",
            codeVerifier = codeVerifier,
            codeChallenge = codeChallenge,
            url = "${API_ENDPOINT}/web/v1/login?${params.formUrlEncode()}"
        )
    }

    override suspend fun getActiveAccount(): PixivUserAccount? {
        val account = userAccountPreference.get() ?: return null
        return if (isActiveAccount(account)) account else refreshAccount(account.refreshToken)
    }

    private fun getCodeVerifier(length: Int): String {
        return (('A'..'Z') + ('a'..'z') + ('0'..'9') + listOf('-', '.', '_', '~')).let { allowedChars ->
            (0 until length).map { allowedChars.random() }.joinToString(separator = "")
        }
    }

    private fun getCodeChallenge(code: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(code.toByteArray(Charsets.US_ASCII))
        val codeChallenge = Base64.getEncoder().encodeToString(bytes)

        return codeChallenge
            .replace("=", "")
            .replace("\\", "-")
            .replace("/", "_")
    }

    private fun isActiveAccount(account: PixivUserAccount): Boolean {
        return account.acquisitionTime.plusSeconds(account.expiresIn.toLong()).isAfter(LocalDateTime.now())
    }

    companion object {
        private const val API_ENDPOINT = "https://app-api.pixiv.net"
        private const val AUTH_ENDPOINT = "https://oauth.secure.pixiv.net"
    }
}
