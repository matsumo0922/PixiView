package caios.android.fanbox.core.model.entity

import caios.android.fanbox.core.common.serializer.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class VersionEntity(
    val versionName: String,
    val versionCode: Int,
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate,
    val logJp: String,
    val logEn: String,
)
