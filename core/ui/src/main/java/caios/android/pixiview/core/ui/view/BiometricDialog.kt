package caios.android.pixiview.core.ui.view

import android.os.Build
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import caios.android.pixiview.core.ui.BuildConfig
import caios.android.pixiview.core.ui.R
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class BiometricDialog(private val context: FragmentActivity) {

    private var executor: Executor
    private var promptInfo: BiometricPrompt.PromptInfo

    init {
        val authentication = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            Authenticators.BIOMETRIC_STRONG or Authenticators.DEVICE_CREDENTIAL
        } else {
            Authenticators.BIOMETRIC_WEAK or Authenticators.DEVICE_CREDENTIAL
        }

        executor = ContextCompat.getMainExecutor(context)

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(context.getString(R.string.home_app_lock_title))
            .setSubtitle(context.getString(R.string.home_app_lock_message))
            .setAllowedAuthenticators(authentication)
            .setConfirmationRequired(false)
            .build()
    }

    suspend fun auth() = suspendCoroutine {
        BiometricPrompt(context, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    it.resume(false)
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    it.resume(true)
                }
            }
        ).also { prompt ->
            prompt.authenticate(promptInfo)
        }
    }
}
