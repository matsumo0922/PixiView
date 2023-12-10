package caios.android.fanbox.core.repository

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.CookieManager
import android.webkit.MimeTypeMap
import caios.android.fanbox.core.datastore.PreferenceBookmarkedPosts
import caios.android.fanbox.core.datastore.PreferenceFanboxCookie
import caios.android.fanbox.core.model.FanboxTag
import caios.android.fanbox.core.model.PageCursorInfo
import caios.android.fanbox.core.model.PageNumberInfo
import caios.android.fanbox.core.model.PageOffsetInfo
import caios.android.fanbox.core.model.fanbox.FanboxBell
import caios.android.fanbox.core.model.fanbox.FanboxCreatorDetail
import caios.android.fanbox.core.model.fanbox.FanboxCreatorPlan
import caios.android.fanbox.core.model.fanbox.FanboxCreatorPlanDetail
import caios.android.fanbox.core.model.fanbox.FanboxCreatorTag
import caios.android.fanbox.core.model.fanbox.FanboxCursor
import caios.android.fanbox.core.model.fanbox.FanboxMetaData
import caios.android.fanbox.core.model.fanbox.FanboxNewsLetter
import caios.android.fanbox.core.model.fanbox.FanboxPaidRecord
import caios.android.fanbox.core.model.fanbox.FanboxPost
import caios.android.fanbox.core.model.fanbox.FanboxPostDetail
import caios.android.fanbox.core.model.fanbox.entity.FanboxBellItemsEntity
import caios.android.fanbox.core.model.fanbox.entity.FanboxCreatorEntity
import caios.android.fanbox.core.model.fanbox.entity.FanboxCreatorItemsEntity
import caios.android.fanbox.core.model.fanbox.entity.FanboxCreatorPlanEntity
import caios.android.fanbox.core.model.fanbox.entity.FanboxCreatorPlansEntity
import caios.android.fanbox.core.model.fanbox.entity.FanboxCreatorPostsPaginateEntity
import caios.android.fanbox.core.model.fanbox.entity.FanboxCreatorSearchEntity
import caios.android.fanbox.core.model.fanbox.entity.FanboxCreatorTagsEntity
import caios.android.fanbox.core.model.fanbox.entity.FanboxMetaDataEntity
import caios.android.fanbox.core.model.fanbox.entity.FanboxNewsLattersEntity
import caios.android.fanbox.core.model.fanbox.entity.FanboxPaidRecordEntity
import caios.android.fanbox.core.model.fanbox.entity.FanboxPostCommentItemsEntity
import caios.android.fanbox.core.model.fanbox.entity.FanboxPostDetailEntity
import caios.android.fanbox.core.model.fanbox.entity.FanboxPostItemsEntity
import caios.android.fanbox.core.model.fanbox.entity.FanboxPostSearchEntity
import caios.android.fanbox.core.model.fanbox.entity.FanboxTagsEntity
import caios.android.fanbox.core.model.fanbox.id.CommentId
import caios.android.fanbox.core.model.fanbox.id.CreatorId
import caios.android.fanbox.core.model.fanbox.id.PostId
import caios.android.fanbox.core.repository.utils.parse
import caios.android.fanbox.core.repository.utils.requireSuccess
import caios.android.fanbox.core.repository.utils.translate
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.jsoup.Jsoup
import java.io.File
import java.io.OutputStream
import java.time.OffsetDateTime
import javax.inject.Inject

interface FanboxRepository {
    val cookie: SharedFlow<String>
    val metaData: StateFlow<FanboxMetaData>
    val bookmarkedPosts: SharedFlow<List<PostId>>
    val logoutTrigger: Flow<OffsetDateTime>

    fun hasActiveCookie(): Boolean

    suspend fun logout()

    suspend fun updateCookie(cookie: String)
    suspend fun updateCsrfToken()

    suspend fun getHomePosts(cursor: FanboxCursor? = null): PageCursorInfo<FanboxPost>
    suspend fun getSupportedPosts(cursor: FanboxCursor? = null): PageCursorInfo<FanboxPost>
    suspend fun getCreatorPosts(creatorId: CreatorId, cursor: FanboxCursor? = null): PageCursorInfo<FanboxPost>
    suspend fun getCreatorPostsPaginate(creatorId: CreatorId): List<FanboxCursor>
    suspend fun getPost(postId: PostId): FanboxPostDetail
    suspend fun getPostCached(postId: PostId): FanboxPostDetail
    suspend fun getPostComment(postId: PostId, offset: Int = 0): PageOffsetInfo<FanboxPostDetail.Comment.CommentItem>

    suspend fun getFollowingCreators(): List<FanboxCreatorDetail>
    suspend fun getFollowingPixivCreators(): List<FanboxCreatorDetail>
    suspend fun getRecommendedCreators(): List<FanboxCreatorDetail>

    suspend fun getCreator(creatorId: CreatorId): FanboxCreatorDetail
    suspend fun getCreatorCached(creatorId: CreatorId): FanboxCreatorDetail
    suspend fun getCreatorTags(creatorId: CreatorId): List<FanboxCreatorTag>

    suspend fun getPostFromQuery(query: String, creatorId: CreatorId? = null, page: Int = 0): PageNumberInfo<FanboxPost>
    suspend fun getCreatorFromQuery(query: String, page: Int = 0): PageNumberInfo<FanboxCreatorDetail>
    suspend fun getTagFromQuery(query: String): List<FanboxTag>

    suspend fun getSupportedPlans(): List<FanboxCreatorPlan>
    suspend fun getCreatorPlans(creatorId: CreatorId): List<FanboxCreatorPlan>
    suspend fun getCreatorPlan(creatorId: CreatorId): FanboxCreatorPlanDetail

    suspend fun getPaidRecords(): List<FanboxPaidRecord>
    suspend fun getUnpaidRecords(): List<FanboxPaidRecord>

    suspend fun getNewsLetters(): List<FanboxNewsLetter>
    suspend fun getBells(page: Int = 0): PageNumberInfo<FanboxBell>

    suspend fun likePost(postId: PostId)
    suspend fun likeComment(commentId: CommentId)

    suspend fun addComment(postId: PostId, comment: String, rootCommentId: CommentId? = null, parentCommentId: CommentId? = null)
    suspend fun deleteComment(commentId: CommentId)

    suspend fun followCreator(creatorUserId: String)
    suspend fun unfollowCreator(creatorUserId: String)

    suspend fun getBookmarkedPosts(): List<FanboxPost>
    suspend fun bookmarkPost(post: FanboxPost)
    suspend fun unbookmarkPost(post: FanboxPost)

    suspend fun downloadBitmap(bitmap: Bitmap, name: String)
    suspend fun downloadImage(url: String, name: String, extension: String, dir: String = "")
    suspend fun downloadFile(url: String, name: String, extension: String, dir: String = "")
}

class FanboxRepositoryImpl(
    private val context: Context,
    private val client: HttpClient,
    private val formatter: Json,
    private val fanboxCookiePreference: PreferenceFanboxCookie,
    private val bookmarkedPostsPreference: PreferenceBookmarkedPosts,
    private val ioDispatcher: CoroutineDispatcher,
) : FanboxRepository {

    private val creatorCache = mutableMapOf<CreatorId, FanboxCreatorDetail>()
    private val postCache = mutableMapOf<PostId, FanboxPostDetail>()

    private val _metaData = MutableStateFlow(FanboxMetaData.dummy())
    private val _logoutTrigger = Channel<OffsetDateTime>()

    override val cookie: SharedFlow<String> = fanboxCookiePreference.data
    override val metaData: StateFlow<FanboxMetaData> = _metaData.asStateFlow()
    override val logoutTrigger: Flow<OffsetDateTime> = _logoutTrigger.receiveAsFlow()

    override val bookmarkedPosts: SharedFlow<List<PostId>> = bookmarkedPostsPreference.data

    @Inject
    constructor(
        @ApplicationContext context: Context,
        client: HttpClient,
        formatter: Json,
        fanboxCookiePreference: PreferenceFanboxCookie,
        bookmarkedPostsPreference: PreferenceBookmarkedPosts,
    ) : this(
        context = context,
        client = client,
        formatter = formatter,
        fanboxCookiePreference = fanboxCookiePreference,
        bookmarkedPostsPreference = bookmarkedPostsPreference,
        ioDispatcher = Dispatchers.IO,
    )

    override fun hasActiveCookie(): Boolean {
        return fanboxCookiePreference.get() != null
    }

    override suspend fun logout() {
        CookieManager.getInstance().removeAllCookies {
            if (it) {
                CoroutineScope(ioDispatcher).launch {
                    fanboxCookiePreference.save("")
                    _logoutTrigger.send(OffsetDateTime.now())
                }
            } else {
                error("Logout failed")
            }
        }
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
            get("post.listHome", it).parse<FanboxPostItemsEntity>()!!.translate(bookmarkedPosts.first())
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
            get("post.listSupporting", it).parse<FanboxPostItemsEntity>()!!.translate(bookmarkedPosts.first())
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
            get("post.listCreator", it).parse<FanboxPostItemsEntity>()!!.translate(bookmarkedPosts.first())
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
            get("post.listTagged", it).parse<FanboxPostSearchEntity>()!!.translate(bookmarkedPosts.first())
        }
    }

    override suspend fun getCreatorPostsPaginate(creatorId: CreatorId): List<FanboxCursor> = withContext(ioDispatcher) {
        get("post.paginateCreator", mapOf("creatorId" to creatorId.value)).parse<FanboxCreatorPostsPaginateEntity>()!!.translate()
    }

    override suspend fun getCreatorFromQuery(query: String, page: Int): PageNumberInfo<FanboxCreatorDetail> = withContext(ioDispatcher) {
        get("creator.search", mapOf("q" to query, "page" to page.toString())).parse<FanboxCreatorSearchEntity>()!!.translate()
    }

    override suspend fun getTagFromQuery(query: String): List<FanboxTag> = withContext(ioDispatcher) {
        get("tag.search", mapOf("q" to query)).parse<FanboxTagsEntity>()!!.translate()
    }

    override suspend fun getPost(postId: PostId): FanboxPostDetail = withContext(ioDispatcher) {
        get("post.info", mapOf("postId" to postId.value)).parse<FanboxPostDetailEntity>()!!.translate(bookmarkedPosts.first()).also {
            postCache[postId] = it
        }
    }

    override suspend fun getPostCached(postId: PostId): FanboxPostDetail = withContext(ioDispatcher) {
        postCache.getOrPut(postId) { getPost(postId) }
    }

    override suspend fun getPostComment(postId: PostId, offset: Int): PageOffsetInfo<FanboxPostDetail.Comment.CommentItem> = withContext(ioDispatcher) {
        get("post.listComments", mapOf("postId" to postId.value, "offset" to offset.toString(), "limit" to "10")).parse<FanboxPostCommentItemsEntity>()!!.translate()
    }

    override suspend fun getFollowingCreators(): List<FanboxCreatorDetail> = withContext(ioDispatcher) {
        get("creator.listFollowing").parse<FanboxCreatorItemsEntity>()!!.translate()
    }

    override suspend fun getFollowingPixivCreators(): List<FanboxCreatorDetail> = withContext(ioDispatcher) {
        get("creator.listPixiv").parse<FanboxCreatorItemsEntity>()!!.translate()
    }

    override suspend fun getRecommendedCreators(): List<FanboxCreatorDetail> = withContext(ioDispatcher) {
        get("creator.listRecommended", mapOf("limit" to PAGE_LIMIT)).parse<FanboxCreatorItemsEntity>()!!.translate()
    }

    override suspend fun getCreator(creatorId: CreatorId): FanboxCreatorDetail = withContext(ioDispatcher) {
        get("creator.get", mapOf("creatorId" to creatorId.value)).parse<FanboxCreatorEntity>()!!.translate().also {
            creatorCache[creatorId] = it
        }
    }

    override suspend fun getCreatorCached(creatorId: CreatorId): FanboxCreatorDetail = withContext(ioDispatcher) {
        creatorCache.getOrPut(creatorId) { getCreator(creatorId) }
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

    override suspend fun likePost(postId: PostId): Unit = withContext(ioDispatcher) {
        post("post.likePost", mapOf("postId" to postId.value)).requireSuccess()
    }

    override suspend fun likeComment(commentId: CommentId): Unit = withContext(ioDispatcher) {
        post("post.likeComment", mapOf("commentId" to commentId.value)).requireSuccess()
    }

    override suspend fun addComment(postId: PostId, comment: String, rootCommentId: CommentId?, parentCommentId: CommentId?): Unit = withContext(ioDispatcher) {
        post(
            dir = "post.addComment",
            parameters = mapOf(
                "postId" to postId.value,
                "rootCommentId" to rootCommentId?.value.orEmpty(),
                "parentCommentId" to parentCommentId?.value.orEmpty(),
                "body" to comment,
            ),
        ).requireSuccess()
    }

    override suspend fun deleteComment(commentId: CommentId): Unit = withContext(ioDispatcher) {
        post("post.deleteComment", mapOf("commentId" to commentId.value)).requireSuccess()
    }

    override suspend fun followCreator(creatorUserId: String): Unit = withContext(ioDispatcher) {
        post("follow.create", mapOf("creatorUserId" to creatorUserId)).requireSuccess()
    }

    override suspend fun unfollowCreator(creatorUserId: String): Unit = withContext(ioDispatcher) {
        post("follow.delete", mapOf("creatorUserId" to creatorUserId)).requireSuccess()
    }

    override suspend fun getBookmarkedPosts(): List<FanboxPost> = withContext(ioDispatcher) {
        bookmarkedPostsPreference.get()
    }

    override suspend fun bookmarkPost(post: FanboxPost) = withContext(ioDispatcher) {
        bookmarkedPostsPreference.save(post)
    }

    override suspend fun unbookmarkPost(post: FanboxPost) {
        bookmarkedPostsPreference.remove(post)
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

    override suspend fun downloadImage(url: String, name: String, extension: String, dir: String) = withContext(ioDispatcher) {
        val mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        val uri = getUri(context, "$name.$extension", Environment.DIRECTORY_PICTURES, dir, mime.orEmpty())
        download(url, context.contentResolver.openOutputStream(uri!!)!!)
    }

    override suspend fun downloadFile(url: String, name: String, extension: String, dir: String) = withContext(ioDispatcher) {
        val mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        val uri = getUri(context, "$name.$extension", Environment.DIRECTORY_DOWNLOADS, dir, mime.orEmpty())
        download(url, context.contentResolver.openOutputStream(uri!!)!!)
    }

    private fun getExternalFile(name: String, parent: String): File {
        val downloadFile = Environment.getExternalStoragePublicDirectory(parent)
        val pixiViewFile = downloadFile.resolve("FANBOX")

        if (!pixiViewFile.exists()) {
            pixiViewFile.mkdirs()
        }

        return pixiViewFile.resolve(name)
    }

    private fun getUri(context: Context, name: String, parent: String, child: String = "", mimeType: String = ""): Uri? {
        val contentResolver = context.contentResolver
        val contentValues = ContentValues().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val path = when {
                    mimeType.contains("image") -> MediaStore.Images.ImageColumns.RELATIVE_PATH
                    mimeType.contains("video") -> MediaStore.Video.VideoColumns.RELATIVE_PATH
                    mimeType.contains("audio") -> MediaStore.Audio.AudioColumns.RELATIVE_PATH
                    else -> MediaStore.Files.FileColumns.RELATIVE_PATH
                }

                put(path, "$parent/FANBOX" + if (child.isBlank()) "" else "/$child")
            } else {
                val path = Environment.getExternalStorageDirectory().path + "/$parent/FANBOX" + if (child.isEmpty()) "" else "/$child"
                val dir = File(Environment.getExternalStorageDirectory().path + "/$parent", "FANBOX")
                val childDir = File(dir, child)

                if (!dir.exists()) {
                    dir.mkdir()
                }

                if (child.isNotBlank() && !childDir.exists()) {
                    childDir.mkdir()
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
