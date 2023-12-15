package caios.android.fanbox.core.logs

import org.json.JSONObject

// This class is automatically generated by generate-log-classes.
data class CommonPayload(
    // ユーザー識別用の UUID
    // データ削除でリセットされる
    val pixiviewId: String,
    // ユーザーエージェント
    // ex) caios.android.fanbox/11; Android/28; SOV36; KDDI; f608ac4c29
    val userAgent: String,
    // FanboxViewer+ ユーザーか
    val isPlus: Boolean,
    // 開発者モードか
    val isDeveloper: Boolean,
    // テスターか
    val isTester: Boolean,
    // Android OSバージョン
    val osVersion: String,
    // アプリケーションのビルドバリアント(release/debug/billing)
    val applicationVariant: String,
    // アプリケーション VersionCode (212700131 のような数値)
    val applicationVersionCode: Long,
    // アプリケーション VersionName (21.27.0.13 のような文字列)
    val applicationVersionName: String,
    // タイムゾーン(Asia/Tokyoなど)
    val timeZone: String,
) {
    fun applyToJsonObject(jsonLog: JSONObject) = jsonLog.apply {
        put("pixiview_id", pixiviewId)
        put("user_agent", userAgent)
        put("is_plus", isPlus)
        put("is_developer", isDeveloper)
        put("is_tester", isTester)
        put("os_version", osVersion)
        put("application_variant", applicationVariant)
        put("application_version_code", applicationVersionCode)
        put("application_version_name", applicationVersionName)
        put("time_zone", timeZone)
    }
}