package caios.android.pixiview.core.repository

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
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
import caios.android.pixiview.core.model.fanbox.FanboxMetaData
import caios.android.pixiview.core.model.fanbox.FanboxNewsLetter
import caios.android.pixiview.core.model.fanbox.FanboxPaidRecord
import caios.android.pixiview.core.model.fanbox.FanboxPost
import caios.android.pixiview.core.model.fanbox.FanboxPostDetail
import caios.android.pixiview.core.model.fanbox.entity.FanboxBellItemsEntity
import caios.android.pixiview.core.model.fanbox.entity.FanboxCreatorEntity
import caios.android.pixiview.core.model.fanbox.entity.FanboxCreatorItemsEntity
import caios.android.pixiview.core.model.fanbox.entity.FanboxCreatorPlanEntity
import caios.android.pixiview.core.model.fanbox.entity.FanboxCreatorPlansEntity
import caios.android.pixiview.core.model.fanbox.entity.FanboxCreatorSearchEntity
import caios.android.pixiview.core.model.fanbox.entity.FanboxCreatorTagsEntity
import caios.android.pixiview.core.model.fanbox.entity.FanboxMetaDataEntity
import caios.android.pixiview.core.model.fanbox.entity.FanboxNewsLattersEntity
import caios.android.pixiview.core.model.fanbox.entity.FanboxPaidRecordEntity
import caios.android.pixiview.core.model.fanbox.entity.FanboxPostDetailEntity
import caios.android.pixiview.core.model.fanbox.entity.FanboxPostItemsEntity
import caios.android.pixiview.core.model.fanbox.entity.FanboxPostSearchEntity
import caios.android.pixiview.core.model.fanbox.id.CreatorId
import caios.android.pixiview.core.model.fanbox.id.PostId
import caios.android.pixiview.core.repository.utils.parse
import caios.android.pixiview.core.repository.utils.translate
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMessageBuilder
import io.ktor.util.InternalAPI
import io.ktor.utils.io.jvm.javaio.copyTo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.jsoup.Jsoup
import java.io.File
import java.io.OutputStream
import javax.inject.Inject

interface FanboxRepository {
    val cookie: SharedFlow<String>
    val metaData: SharedFlow<FanboxMetaData>

    fun hasActiveCookie(): Boolean

    suspend fun updateCookie(cookie: String)
    suspend fun updateCsrfToken()

    suspend fun getHomePosts(cursor: FanboxCursor? = null): PageCursorInfo<FanboxPost>
    suspend fun getSupportedPosts(cursor: FanboxCursor? = null): PageCursorInfo<FanboxPost>
    suspend fun getCreatorPosts(creatorId: CreatorId, cursor: FanboxCursor? = null): PageCursorInfo<FanboxPost>
    suspend fun getPost(postId: PostId): FanboxPostDetail
    suspend fun getPostCached(postId: PostId): FanboxPostDetail

    suspend fun getFollowingCreators(): List<FanboxCreatorDetail>
    suspend fun getRecommendedCreators(): List<FanboxCreatorDetail>

    suspend fun getCreator(creatorId: CreatorId): FanboxCreatorDetail
    suspend fun getCreatorTags(creatorId: CreatorId): List<FanboxCreatorTag>

    suspend fun getPostFromQuery(query: String, creatorId: CreatorId? = null, page: Int = 0): PageNumberInfo<FanboxPost>
    suspend fun getCreatorFromQuery(query: String, page: Int = 0): PageNumberInfo<FanboxCreatorDetail>

    suspend fun getSupportedPlans(): List<FanboxCreatorPlan>
    suspend fun getCreatorPlans(creatorId: CreatorId): List<FanboxCreatorPlan>
    suspend fun getCreatorPlan(creatorId: CreatorId): FanboxCreatorPlanDetail

    suspend fun getPaidRecords(): List<FanboxPaidRecord>
    suspend fun getUnpaidRecords(): List<FanboxPaidRecord>

    suspend fun getNewsLetters(): List<FanboxNewsLetter>

    suspend fun getBells(page: Int = 0): PageNumberInfo<FanboxBell>

    suspend fun followCreator(creatorUserId: String)
    suspend fun unfollowCreator(creatorUserId: String)

    suspend fun downloadBitmap(bitmap: Bitmap, name: String)
    suspend fun downloadImage(url: String, name: String, extension: String)
    suspend fun downloadFile(url: String, name: String, extension: String)
}

class FanboxRepositoryImpl(
    private val context: Context,
    private val client: HttpClient,
    private val formatter: Json,
    private val fanboxCookiePreference: PreferenceFanboxCookie,
    private val ioDispatcher: CoroutineDispatcher,
) : FanboxRepository {

    private val _metaData = MutableSharedFlow<FanboxMetaData>(replay = 1)
    private val postCache = mutableMapOf<PostId, FanboxPostDetail>()

    override val cookie: SharedFlow<String> = fanboxCookiePreference.data
    override val metaData: SharedFlow<FanboxMetaData> = _metaData.asSharedFlow()

    @Inject
    constructor(
        @ApplicationContext context: Context,
        client: HttpClient,
        formatter: Json,
        fanboxCookiePreference: PreferenceFanboxCookie,
    ) : this(
        context = context,
        client = client,
        formatter = formatter,
        fanboxCookiePreference = fanboxCookiePreference,
        ioDispatcher = Dispatchers.IO,
    )

    override fun hasActiveCookie(): Boolean {
        return fanboxCookiePreference.get() != null
    }

    override suspend fun updateCookie(cookie: String) {
        fanboxCookiePreference.save(cookie)
    }

    override suspend fun updateCsrfToken() = withContext(ioDispatcher) {
        val html = html("https://www.fanbox.cc/")
        val doc = Jsoup.parse(html)
        val meta = doc.select("meta[name=metadata]").first()?.attr("content")?.toString()

        _metaData.emit(formatter.decodeFromString(FanboxMetaDataEntity.serializer(), meta!!).translate())
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

    override suspend fun getPostFromQuery(query: String, creatorId: CreatorId?, page: Int): PageNumberInfo<FanboxPost> = withContext(ioDispatcher) {
        buildMap {
            put("tag", query)
            put("page", page.toString())

            if (creatorId != null) {
                put("creatorId", creatorId.value)
            }
        }.let {
            get("post.listTagged", it).parse<FanboxPostSearchEntity>()!!.translate()
        }
    }

    override suspend fun getCreatorFromQuery(query: String, page: Int): PageNumberInfo<FanboxCreatorDetail> = withContext(ioDispatcher) {
        get("creator.search", mapOf("q" to query, "page" to page.toString())).parse<FanboxCreatorSearchEntity>()!!.translate()
    }

    override suspend fun getPost(postId: PostId): FanboxPostDetail = withContext(ioDispatcher) {
        get("post.info", mapOf("postId" to postId.value)).parse<FanboxPostDetailEntity>()!!.translate().also {
            postCache[postId] = it
        }
    }

    override suspend fun getPostCached(postId: PostId): FanboxPostDetail = withContext(ioDispatcher) {
        postCache.getOrPut(postId) { getPost(postId) }
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

    override suspend fun followCreator(creatorUserId: String): Unit = withContext(ioDispatcher) {
        post("follow.create", mapOf("creatorUserId" to creatorUserId))
    }

    override suspend fun unfollowCreator(creatorUserId: String): Unit = withContext(ioDispatcher) {
        post("follow.delete", mapOf("creatorUserId" to creatorUserId))
    }

    override suspend fun downloadBitmap(bitmap: Bitmap, name: String): Unit = withContext(ioDispatcher) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            val mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension("jpg")
            val itemFile = getExternalFile("$name.jpg", Environment.DIRECTORY_PICTURES)

            itemFile.outputStream().use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }

            MediaScannerConnection.scanFile(context, arrayOf(itemFile.absolutePath), arrayOf(mime), null)
        } else {
            val uri = getUri(context, "$name.jpg", Environment.DIRECTORY_PICTURES)

            context.contentResolver.openOutputStream(uri!!)!!.use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }
        }
    }

    override suspend fun downloadImage(url: String, name: String, extension: String) = withContext(ioDispatcher) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            val mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            val itemFile = getExternalFile("$name.$extension", Environment.DIRECTORY_PICTURES)

            download(url, itemFile.outputStream())
            MediaScannerConnection.scanFile(context, arrayOf(itemFile.absolutePath), arrayOf(mime), null)
        } else {
            val uri = getUri(context, "$name.$extension", Environment.DIRECTORY_PICTURES)
            download(url, context.contentResolver.openOutputStream(uri!!)!!)
        }
    }

    override suspend fun downloadFile(url: String, name: String, extension: String) = withContext(ioDispatcher) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            val itemFile = getExternalFile("$name.$extension", Environment.DIRECTORY_DOWNLOADS)
            download(url, itemFile.outputStream())
        } else {
            val uri = getUri(context, "$name.$extension", Environment.DIRECTORY_DOWNLOADS)
            download(url, context.contentResolver.openOutputStream(uri!!)!!)
        }
    }

    private fun getExternalFile(name: String, parent: String): File {
        val downloadFile = Environment.getExternalStoragePublicDirectory(parent)
        val pixiViewFile = downloadFile.resolve("PixiView")

        if (!pixiViewFile.exists()) {
            pixiViewFile.mkdirs()
        }

        return pixiViewFile.resolve(name)
    }

    private fun getUri(context: Context, name: String, parent: String): Uri? {
        val contentResolver = context.contentResolver
        val contentValues = ContentValues().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.ImageColumns.RELATIVE_PATH, "$parent/PixiView")
            } else {
                val path = Environment.getExternalStorageDirectory().path + "/$parent/PixiView/"
                val dir = File(Environment.getExternalStorageDirectory().path + "/$parent", "PixiView")

                if (!dir.exists()) {
                    dir.mkdir()
                }

                put(MediaStore.Images.ImageColumns.DATA, path + name)
            }
            put(MediaStore.Images.ImageColumns.DISPLAY_NAME, name)
            put(MediaStore.Images.ImageColumns.DATE_TAKEN, System.currentTimeMillis())
        }

        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    }

    private suspend fun html(url: String): String {
        return client.get(url).bodyAsText()
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

    @OptIn(InternalAPI::class)
    private suspend fun post(dir: String, parameters: Map<String, String> = emptyMap()): HttpResponse {
        return client.post {
            url("$API/$dir")
            fanboxHeader()

            body = buildJsonObject {
                for ((key, value) in parameters) {
                    put(key, value)
                }
            }.toString()
        }
    }

    private suspend fun download(url: String, outputStream: OutputStream) {
        client.get {
            url(url)
            fanboxHeader()
        }
            .bodyAsChannel()
            .copyTo(outputStream)
    }

    private suspend fun HttpMessageBuilder.fanboxHeader() {
        header("origin", "https://www.fanbox.cc")
        header("referer", "https://www.fanbox.cc")
        header("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36")
        header("Content-Type", "application/json")
        header("x-csrf-token", metaData.first().csrfToken)
        header("Cookie", cookie.first())
    }

    companion object {
        private const val API = "https://api.fanbox.cc"
        private const val PAGE_LIMIT = "10"
    }
}
