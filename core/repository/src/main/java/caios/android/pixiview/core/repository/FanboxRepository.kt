package caios.android.pixiview.core.repository

import caios.android.pixiview.core.datastore.PreferenceFanboxCookie
import caios.android.pixiview.core.model.PageInfo
import caios.android.pixiview.core.model.fanbox.FanboxCreator
import caios.android.pixiview.core.model.fanbox.FanboxCreatorPlan
import caios.android.pixiview.core.model.fanbox.FanboxCreatorTag
import caios.android.pixiview.core.model.fanbox.FanboxCursor
import caios.android.pixiview.core.model.fanbox.FanboxPost
import caios.android.pixiview.core.model.fanbox.FanboxPostDetail
import caios.android.pixiview.core.model.fanbox.entity.FanboxCreatorEntity
import caios.android.pixiview.core.model.fanbox.entity.FanboxCreatorItemsEntity
import caios.android.pixiview.core.model.fanbox.entity.FanboxCreatorPlansEntity
import caios.android.pixiview.core.model.fanbox.entity.FanboxCreatorTagsEntity
import caios.android.pixiview.core.model.fanbox.entity.FanboxPostDetailEntity
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
    suspend fun getSupportedPosts(cursor: FanboxCursor? = null): PageInfo<FanboxPost>?
    suspend fun getCreatorPosts(creatorId: String, cursor: FanboxCursor? = null): PageInfo<FanboxPost>?
    suspend fun getPost(postId: String): FanboxPostDetail?

    suspend fun getFollowingCreators(): List<FanboxCreator>?
    suspend fun getRecommendedCreators(): List<FanboxCreator>?

    suspend fun getCreator(creatorId: String): FanboxCreator?
    suspend fun getCreatorTags(creatorId: String): List<FanboxCreatorTag>?

    suspend fun getSupportedPlans(): List<FanboxCreatorPlan>?
    suspend fun getCreatorPlans(creatorId: String): List<FanboxCreatorPlan>?
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
        return buildMap {
            put("limit", "10")

            if (cursor != null) {
                put("maxPublishedDatetime", cursor.maxPublishedDatetime)
                put("maxId", cursor.maxId)
            }
        }.let {
            get("post.listHome", it).parse<FanboxPostItemsEntity>()?.translate()
        }
    }

    override suspend fun getSupportedPosts(cursor: FanboxCursor?): PageInfo<FanboxPost>? {
        return buildMap {
            put("limit", "10")

            if (cursor != null) {
                put("maxPublishedDatetime", cursor.maxPublishedDatetime)
                put("maxId", cursor.maxId)
            }
        }.let {
            get("post.listSupporting", it).parse<FanboxPostItemsEntity>()?.translate()
        }
    }

    override suspend fun getCreatorPosts(creatorId: String, cursor: FanboxCursor?): PageInfo<FanboxPost>? {
        return buildMap {
            put("creatorId", creatorId)
            put("limit", "10")

            if (cursor != null) {
                put("maxPublishedDatetime", cursor.maxPublishedDatetime)
                put("maxId", cursor.maxId)
            }
        }.let {
            get("post.listCreator", it).parse<FanboxPostItemsEntity>()?.translate()
        }
    }

    override suspend fun getPost(postId: String): FanboxPostDetail? {
        return get("post.info", mapOf("postId" to postId)).parse<FanboxPostDetailEntity>()?.translate()
    }

    override suspend fun getFollowingCreators(): List<FanboxCreator>? {
        return get("creator.listFollowing").parse<FanboxCreatorItemsEntity>()?.translate()
    }

    override suspend fun getRecommendedCreators(): List<FanboxCreator>? {
        return get("creator.listRecommended").parse<FanboxCreatorItemsEntity>()?.translate()
    }

    override suspend fun getCreator(creatorId: String): FanboxCreator? {
        return get("creator.get", mapOf("creatorId" to creatorId)).parse<FanboxCreatorEntity>()?.translate()
    }

    override suspend fun getCreatorTags(creatorId: String): List<FanboxCreatorTag>? {
        return get("tag.getFeatured", mapOf("creatorId" to creatorId)).parse<FanboxCreatorTagsEntity>()?.translate()
    }

    override suspend fun getSupportedPlans(): List<FanboxCreatorPlan>? {
        return get("plan.listSupporting").parse<FanboxCreatorPlansEntity>()?.translate()
    }

    override suspend fun getCreatorPlans(creatorId: String): List<FanboxCreatorPlan>? {
        return get("plan.listCreator", mapOf("creatorId" to creatorId)).parse<FanboxCreatorPlansEntity>()?.translate()
    }

    private suspend fun get(dir: String, parameters: Map<String, String> = emptyMap()): HttpResponse {
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
