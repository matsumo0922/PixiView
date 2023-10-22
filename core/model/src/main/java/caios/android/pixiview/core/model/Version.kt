package caios.android.pixiview.core.model

import caios.android.pixiview.core.common.serializer.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class Version(
    val name: String,
    val code: Int,
    val message: String,
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate,
)
