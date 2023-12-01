package caios.android.fanbox.core.datastore

import android.content.Context
import caios.android.fanbox.core.model.fanbox.FanboxPost
import caios.android.fanbox.core.model.fanbox.id.PostId
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject

class PreferenceBookmarkedPosts @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val accountFile get() = File(context.filesDir, FILE_NAME)
    private val serializer get() = ListSerializer(FanboxPost.serializer())

    private val _data = MutableSharedFlow<List<PostId>>(replay = 1)

    val data = _data.asSharedFlow()

    init {
        _data.tryEmit(get().map { it.id })
    }

    fun save(post: FanboxPost) {
        val posts = get().toMutableList().apply {
            add(0, post.copy(isBookmarked = true))
        }

        Json.encodeToString(serializer, posts).also {
            accountFile.writeText(it)
        }

        _data.tryEmit(posts.map { it.id })
    }

    fun remove(post: FanboxPost) {
        val posts = get().toMutableList().apply {
            removeIf { it.id == post.id }
        }

        Json.encodeToString(serializer, posts).also {
            accountFile.writeText(it)
        }

        _data.tryEmit(posts.map { it.id })
    }

    fun get(): List<FanboxPost> {
        if (!accountFile.exists()) return emptyList()

        return accountFile.readText().let { text ->
            Json.decodeFromString(serializer, text).distinctBy { it.id }
        }
    }

    companion object {
        private const val FILE_NAME = "BookmarkedPosts.json"
    }
}
