package caios.android.pixiview.core.repository

import android.content.Context
import android.media.MediaScannerConnection
import android.os.Environment
import android.webkit.MimeTypeMap
import caios.android.pixiview.core.datastore.PreferenceFanboxCookie
import caios.android.pixiview.core.model.PageCursorInfo
import caios.android.pixiview.core.model.PageNumberInfo
import caios.android.pixiview.core.model.fanbox.FanboxBell
import caios.android.pixiview.core.model.fanbox.FanboxCreatorDetail
import caios.android.pixiview.core.model.fanbox.FanboxCreatorPlan
import caios.android.pixiview.core.model.fanbox.FanboxCreatorPlanDetail
import caios.android.pixiview.core.model.fanbox.FanboxCreatorTag
import caios.android.pixiview.core.model.fanbox.FanboxCursor
import caios.android.pixiview.core.model.fanbox.FanboxNewsLetter
import caios.android.pixiview.core.model.fanbox.FanboxPaidRecord
import caios.android.pixiview.core.model.fanbox.FanboxPost
import caios.android.pixiview.core.model.fanbox.FanboxPostDetail
import caios.android.pixiview.core.model.fanbox.entity.FanboxBellItemsEntity
import caios.android.pixiview.core.model.fanbox.entity.FanboxCreatorEntity
import caios.android.pixiview.core.model.fanbox.entity.FanboxCreatorItemsEntity
import caios.android.pixiview.core.model.fanbox.entity.FanboxCreatorPlanEntity
import caios.android.pixiview.core.model.fanbox.entity.FanboxCreatorPlansEntity
import caios.android.pixiview.core.model.fanbox.entity.FanboxCreatorTagsEntity
import caios.android.pixiview.core.model.fanbox.entity.FanboxNewsLattersEntity
import caios.android.pixiview.core.model.fanbox.entity.FanboxPaidRecordEntity
import caios.android.pixiview.core.model.fanbox.entity.FanboxPostDetailEntity
import caios.android.pixiview.core.model.fanbox.entity.FanboxPostItemsEntity
import caios.android.pixiview.core.model.fanbox.id.CreatorId
import caios.android.pixiview.core.model.fanbox.id.PostId
import caios.android.pixiview.core.repository.utils.parse
import caios.android.pixiview.core.repository.utils.translate
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMessageBuilder
import io.ktor.http.Parameters
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.copyAndClose
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

interface FanboxRepository {
    val cookie: SharedFlow<String>

    fun hasActiveCookie(): Boolean

    suspend fun updateCookie(cookie: String)

    suspend fun getHomePosts(cursor: FanboxCursor? = null): PageCursorInfo<FanboxPost>
    suspend fun getSupportedPosts(cursor: FanboxCursor? = null): PageCursorInfo<FanboxPost>
    suspend fun getCreatorPosts(creatorId: CreatorId, cursor: FanboxCursor? = null): PageCursorInfo<FanboxPost>
    suspend fun getPost(postId: PostId): FanboxPostDetail

    suspend fun getFollowingCreators(): List<FanboxCreatorDetail>
    suspend fun getRecommendedCreators(): List<FanboxCreatorDetail>

    suspend fun getCreator(creatorId: CreatorId): FanboxCreatorDetail
    suspend fun getCreatorTags(creatorId: CreatorId): List<FanboxCreatorTag>

    suspend fun getSupportedPlans(): List<FanboxCreatorPlan>
    suspend fun getCreatorPlans(creatorId: CreatorId): List<FanboxCreatorPlan>
    suspend fun getCreatorPlan(creatorId: CreatorId): FanboxCreatorPlanDetail

    suspend fun getPaidRecords(): List<FanboxPaidRecord>
    suspend fun getUnpaidRecords(): List<FanboxPaidRecord>

    suspend fun getNewsLetters(): List<FanboxNewsLetter>

    suspend fun getBells(page: Int = 1): PageNumberInfo<FanboxBell>

    suspend fun followCreator(creatorUserId: String)
    suspend fun unfollowCreator(creatorUserId: String)

    suspend fun downloadImage(url: String, name: String, extension: String)
    suspend fun downloadFile(url: String, name: String, extension: String)
}

class FanboxRepositoryImpl(
    private val context: Context,
    private val client: HttpClient,
    private val fanboxCookiePreference: PreferenceFanboxCookie,
    private val ioDispatcher: CoroutineDispatcher,
) : FanboxRepository {

    override val cookie: SharedFlow<String> = fanboxCookiePreference.data

    @Inject
    constructor(
        @ApplicationContext context: Context,
        client: HttpClient,
        fanboxCookiePreference: PreferenceFanboxCookie,
    ) : this(
        context = context,
        client = client,
        fanboxCookiePreference = fanboxCookiePreference,
        ioDispatcher = Dispatchers.IO,
    )

    override fun hasActiveCookie(): Boolean {
        return fanboxCookiePreference.get() != null
    }

    override suspend fun updateCookie(cookie: String) {
        fanboxCookiePreference.save(cookie)
    }

    override suspend fun getHomePosts(cursor: FanboxCursor?): PageCursorInfo<FanboxPost> = withContext(ioDispatcher) {
        buildMap {
            put("limit", PAGE_LIMIT)

            if (cursor != null) {
                put("maxPublishedDatetime", cursor.maxPublishedDatetime)
                put("maxId", cursor.maxId)
            }
        }.let {
            get("post.listHome", it).parse<FanboxPostItemsEntity>()!!.translate()
        }
    }

    override suspend fun getSupportedPosts(cursor: FanboxCursor?): PageCursorInfo<FanboxPost> = withContext(ioDispatcher) {
        buildMap {
            put("limit", PAGE_LIMIT)

            if (cursor != null) {
                put("maxPublishedDatetime", cursor.maxPublishedDatetime)
                put("maxId", cursor.maxId)
            }
        }.let {
            get("post.listSupporting", it).parse<FanboxPostItemsEntity>()!!.translate()
        }
    }

    override suspend fun getCreatorPosts(creatorId: CreatorId, cursor: FanboxCursor?): PageCursorInfo<FanboxPost> = withContext(ioDispatcher) {
        buildMap {
            put("creatorId", creatorId.value)
            put("limit", PAGE_LIMIT)

            if (cursor != null) {
                put("maxPublishedDatetime", cursor.maxPublishedDatetime)
                put("maxId", cursor.maxId)
            }
        }.let {
            get("post.listCreator", it).parse<FanboxPostItemsEntity>()!!.translate()
        }
    }

    override suspend fun getPost(postId: PostId): FanboxPostDetail = withContext(ioDispatcher) {
        get("post.info", mapOf("postId" to postId.value)).parse<FanboxPostDetailEntity>()!!.translate()
    }

    override suspend fun getFollowingCreators(): List<FanboxCreatorDetail> = withContext(ioDispatcher) {
        get("creator.listFollowing").parse<FanboxCreatorItemsEntity>()!!.translate()
    }

    override suspend fun getRecommendedCreators(): List<FanboxCreatorDetail> = withContext(ioDispatcher) {
        get("creator.listRecommended", mapOf("limit" to PAGE_LIMIT)).parse<FanboxCreatorItemsEntity>()!!.translate()
    }

    override suspend fun getCreator(creatorId: CreatorId): FanboxCreatorDetail = withContext(ioDispatcher) {
        get("creator.get", mapOf("creatorId" to creatorId.value)).parse<FanboxCreatorEntity>()!!.translate()
    }

    override suspend fun getCreatorTags(creatorId: CreatorId): List<FanboxCreatorTag> = withContext(ioDispatcher) {
        get("tag.getFeatured", mapOf("creatorId" to creatorId.value)).parse<FanboxCreatorTagsEntity>()!!.translate()
    }

    override suspend fun getSupportedPlans(): List<FanboxCreatorPlan> = withContext(ioDispatcher) {
        get("plan.listSupporting").parse<FanboxCreatorPlansEntity>()!!.translate()
    }

    override suspend fun getCreatorPlans(creatorId: CreatorId): List<FanboxCreatorPlan> = withContext(ioDispatcher) {
        get("plan.listCreator", mapOf("creatorId" to creatorId.value)).parse<FanboxCreatorPlansEntity>()!!.translate()
    }

    override suspend fun getCreatorPlan(creatorId: CreatorId): FanboxCreatorPlanDetail = withContext(ioDispatcher) {
        get("legacy/support/creator", mapOf("creatorId" to creatorId.value)).parse<FanboxCreatorPlanEntity>()!!.translate()
    }

    override suspend fun getPaidRecords(): List<FanboxPaidRecord> = withContext(ioDispatcher) {
        get("payment.listPaid").parse<FanboxPaidRecordEntity>()!!.translate()
    }

    override suspend fun getUnpaidRecords(): List<FanboxPaidRecord> = withContext(ioDispatcher) {
        get("payment.listUnpaid").parse<FanboxPaidRecordEntity>()!!.translate()
    }

    override suspend fun getNewsLetters(): List<FanboxNewsLetter> = withContext(ioDispatcher) {
        get("newsletter.list").parse<FanboxNewsLattersEntity>()!!.translate()
    }

    override suspend fun getBells(page: Int): PageNumberInfo<FanboxBell> = withContext(ioDispatcher) {
        buildMap {
            put("page", page.toString())
            put("skipConvertUnreadNotification", "0")
            put("commentOnly", "0")
        }.let {
            get("bell.list", it).parse<FanboxBellItemsEntity>()!!.translate()
        }
    }

    @Deprecated("動作未確認")
    override suspend fun followCreator(creatorUserId: String): Unit = withContext(ioDispatcher) {
        post("follow.create", mapOf("creatorUserId" to creatorUserId))
    }

    @Deprecated("動作未確認")
    override suspend fun unfollowCreator(creatorUserId: String): Unit = withContext(ioDispatcher) {
        post("follow.delete", mapOf("creatorUserId" to creatorUserId))
    }

    override suspend fun downloadImage(url: String, name: String, extension: String) = withContext(ioDispatcher) {
        val mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        val pictureFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val pixiViewFile = pictureFile.resolve("PixiView")

        if (!pixiViewFile.exists()) {
            pixiViewFile.mkdirs()
        }

        val itemFile = pixiViewFile.resolve("$name.$extension")

        download(url, itemFile.writeChannel())
        MediaScannerConnection.scanFile(context, arrayOf(itemFile.absolutePath), arrayOf(mime), null)
    }

    override suspend fun downloadFile(url: String, name: String, extension: String) = withContext(ioDispatcher) {
        val mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        val downloadFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val pixiViewFile = downloadFile.resolve("PixiView")

        if (!pixiViewFile.exists()) {
            pixiViewFile.mkdirs()
        }

        val itemFile = pixiViewFile.resolve("$name.$extension")

        download(url, itemFile.writeChannel())
        MediaScannerConnection.scanFile(context, arrayOf(itemFile.absolutePath), arrayOf(mime), null)
    }

    private suspend fun get(dir: String, parameters: Map<String, String> = emptyMap()): HttpResponse {
        return client.get {
            url("$API/$dir")
            fanboxHeader()

            for ((key, value) in parameters) {
                parameter(key, value)
            }
        }
    }

    private suspend fun post(dir: String, parameters: Map<String, String> = emptyMap()): HttpResponse {
        return client.submitForm(
            url = "$API/$dir",
            formParameters = Parameters.build {
                for ((key, value) in parameters) {
                    append(key, value)
                }
            },
        ) {
            runBlocking {
                fanboxHeader()
            }
        }
    }

    private suspend fun download(url: String, writeChannel: ByteWriteChannel) {
        client.get {
            url(url)
            fanboxHeader()
        }
            .bodyAsChannel()
            .copyAndClose(writeChannel)
    }

    private suspend fun HttpMessageBuilder.fanboxHeader() {
        header("origin", "https://www.fanbox.cc")
        header("referer", "https://www.fanbox.cc")
        header("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36")
        header("Cookie", cookie.first())
    }

    companion object {
        private const val API = "https://api.fanbox.cc"
        private const val PAGE_LIMIT = "10"
    }
}
