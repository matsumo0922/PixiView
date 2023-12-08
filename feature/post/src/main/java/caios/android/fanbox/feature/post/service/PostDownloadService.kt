package caios.android.fanbox.feature.post.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import caios.android.fanbox.core.model.NotificationConfigs
import caios.android.fanbox.core.model.fanbox.FanboxPost
import caios.android.fanbox.core.model.fanbox.FanboxPostDetail
import caios.android.fanbox.core.model.fanbox.id.PostId
import caios.android.fanbox.core.repository.FanboxRepository
import caios.android.fanbox.feature.post.BuildConfig
import caios.android.fanbox.feature.post.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class PostDownloadService : Service() {

    @Inject
    lateinit var fanboxRepository: FanboxRepository

    private val manager by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
    private val notifyConfig = NotificationConfigs.download

    private var downloadedItemCount = 0
    private var downloadedItemSize = 0
    private var isForeground = false

    private val binder = PostDownloadBinder()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val downloadStack = mutableListOf<DownloadItem>()
    private val downloadLooper = scope.launch(start = CoroutineStart.LAZY) {
        createNotifyChannel()

        while (isActive) {
            try {
                val item = downloadStack.firstOrNull()

                if (item != null) {
                    downloadedItemCount++
                    downloadedItemSize = if (downloadedItemSize == 0) downloadStack.size else downloadedItemSize

                    setForegroundService(
                        isForeground = true,
                        title = getString(R.string.notify_download_title),
                        message = getString(R.string.notify_download_message, downloadStack.size),
                        subMessage = "%.2f%%".format((downloadedItemCount.toFloat() / downloadedItemSize) * 100),
                    )

                    when (item) {
                        is DownloadItem.Image -> fanboxRepository.downloadImage(item.url, item.name, item.extension)
                        is DownloadItem.File -> fanboxRepository.downloadFile(item.url, item.name, item.extension)
                        is DownloadItem.Post -> {
                            val post = fanboxRepository.getPost(item.postId)

                            for (image in post.body.imageItems) {
                                fanboxRepository.downloadImage(image.originalUrl, "illust-${image.postId}-${image.id}", image.extension, post.user.name)
                            }

                            if (!item.isIgnoreFile) {
                                for (file in post.body.fileItems) {
                                    fanboxRepository.downloadFile(file.url, "file-${file.postId}-${file.id}", file.extension, post.user.name)
                                }
                            }
                        }
                    }

                    downloadStack.removeFirstOrNull()
                } else {
                    downloadedItemCount = 0
                    downloadedItemSize = 0

                    setForegroundService(false)
                }

                delay(if (BuildConfig.DEBUG) 10 else 500)
            } catch (e: Throwable) {
                Timber.w(e, "Cannot download item: ${downloadStack.firstOrNull()}")
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        downloadLooper.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    fun downloadImages(imageItems: List<FanboxPostDetail.ImageItem>) {
        downloadStack += imageItems.map {
            DownloadItem.Image(
                name = "illust-${it.postId}-${it.id}",
                extension = it.extension,
                url = if (it.extension.lowercase() == "gif") it.originalUrl else it.thumbnailUrl,
            )
        }
    }

    fun downloadFile(fileItem: FanboxPostDetail.FileItem) {
        downloadStack += DownloadItem.File(
            name = "file-${fileItem.postId}-${fileItem.id}",
            extension = fileItem.extension,
            url = fileItem.url,
        )
    }

    fun downloadPosts(posts: List<FanboxPost>, isIgnoreFree: Boolean, isIgnoreFile: Boolean) {
        downloadStack += posts
            .filter { if (isIgnoreFree) it.feeRequired != 0 else true }
            .map {
                DownloadItem.Post(
                    postId = it.id,
                    isIgnoreFile = isIgnoreFile,
                )
            }
    }

    private fun setForegroundService(
        isForeground: Boolean,
        title: String = "",
        message: String = "",
        subMessage: String = "",
    ) {
        if (isForeground) {
            if (!this.isForeground) {
                startForeground(notifyConfig.notifyId, createNotify(baseContext, title, message, subMessage))
            } else {
                manager.notify(notifyConfig.notifyId, createNotify(baseContext, title, message, subMessage))
            }
        } else {
            stopForeground(STOP_FOREGROUND_REMOVE)
        }

        this.isForeground = isForeground
    }

    private fun createNotify(context: Context, title: String, message: String, subMessage: String): Notification {
        return NotificationCompat.Builder(context, notifyConfig.channelId)
            .setSmallIcon(R.drawable.vec_app_icon_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setSubText(subMessage)
            .setAutoCancel(false)
            .setColorized(true)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
    }

    private fun createNotifyChannel() {
        if (manager.getNotificationChannel(notifyConfig.channelId) != null) return

        val channelName = baseContext.getString(R.string.notify_download_channel_name)
        val channelDescription = baseContext.getString(R.string.notify_download_channel_description)

        val channel = NotificationChannel(
            notifyConfig.channelId,
            channelName,
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            description = channelDescription
        }

        manager.createNotificationChannel(channel)
    }

    private interface DownloadItem {
        data class Image(
            val name: String,
            val extension: String,
            val url: String,
        ) : DownloadItem

        data class File(
            val name: String,
            val extension: String,
            val url: String,
        ) : DownloadItem

        data class Post(
            val postId: PostId,
            val isIgnoreFile: Boolean,
        ) : DownloadItem
    }

    inner class PostDownloadBinder : Binder() {
        fun getService(): PostDownloadService = this@PostDownloadService
    }
}
