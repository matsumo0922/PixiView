package caios.android.pixiview.core.repository

import io.ktor.client.HttpClient
import javax.inject.Inject

interface PixivRepository {

}

class PixivRepositoryImpl @Inject constructor(
    private val client: HttpClient,
): PixivRepository {

}
