package caios.android.pixiview.feature.post.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import caios.android.pixiview.core.common.util.suspendRunCatching
import caios.android.pixiview.core.model.NotificationConfigs
import caios.android.pixiview.core.model.fanbox.FanboxPost
import caios.android.pixiview.core.model.fanbox.FanboxPostDetail
import caios.android.pixiview.core.model.fanbox.id.PostId
import caios.android.pixiview.core.repository.FanboxRepository
import caios.android.pixiview.feature.post.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@AndroidEntryPoint
class PostDownloadService : Service() {

    @Inject
    lateinit var fanboxRepository: FanboxRepository

    private val manager by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
    private val notifyConfig = NotificationConfigs.download
    private var isForeground = false

    private val binder = PostDownloadBinder()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val mutex = Mutex()

    private val _downloadedEvent = Channel<PostId>()

    val downloadedEvent = _downloadedEvent.receiveAsFlow()

    override fun onBind(intent: Intent?): IBinder = binder

    fun downloadImages(imageItems: List<FanboxPostDetail.ImageItem>) {
        scope.launch {
            mutex.withLock {
                createNotifyChannel()

                for (item in imageItems) {
                    val name = "illust-${item.postId}-${item.id}"

                    setForegroundService(true, "$name.${item.extension}")

                    suspendRunCatching {
                        fanboxRepository.downloadImage(
                            url = if (item.extension.lowercase() == "gif") item.originalUrl else item.thumbnailUrl,
                            name = name,
                            extension = item.extension,
                        )
                    }
                }

                _downloadedEvent.send(imageItems.last().postId)
                setForegroundService(false)
            }
        }
    }

    fun downloadFile(fileItem: FanboxPostDetail.FileItem) {
        scope.launch {
            mutex.withLock {
                createNotifyChannel()

                val name = "file-${fileItem.postId}-${fileItem.id}"

                setForegroundService(true, "$name.${fileItem.extension}")

                suspendRunCatching {
                    fanboxRepository.downloadFile(
                        url = fileItem.url,
                        name = name,
                        extension = fileItem.extension,
                    )
                }

                _downloadedEvent.send(fileItem.postId)
                setForegroundService(false)
            }
        }
    }

    fun downloadPosts(posts: List<FanboxPost>, isIgnoreFree: Boolean, isIgnoreFile: Boolean) {
        scope.launch {
            mutex.withLock {
                createNotifyChannel()

                for ((index, post) in posts.withIndex()) {
                    if (isIgnoreFree && post.feeRequired == 0) continue

                    val postDetail = fanboxRepository.getPost(post.id)
                    val title = getString(R.string.creator_posts_download_notify_title, post.user.name)
                    val subMessage = "%.2f %%".format((index + 1).toFloat() / posts.size * 100)

                    for (item in postDetail.body.imageItems) {
                        val name = "illust-${item.postId}-${item.id}"

                        setForegroundService(true, title, "$name.${item.extension}", subMessage)

                        suspendRunCatching {
                            fanboxRepository.downloadImage(
                                url = if (item.extension.lowercase() == "gif") item.originalUrl else item.thumbnailUrl,
                                name = name,
                                extension = item.extension,
                                dir = post.user.creatorId.value,
                            )
                        }
                    }

                    if (!isIgnoreFile) {
                        for (item in postDetail.body.fileItems) {
                            val name = "file-${item.postId}-${item.id}"

                            setForegroundService(true, title, "$name.${item.extension}", subMessage)

                            suspendRunCatching {
                                fanboxRepository.downloadFile(
                                    url = item.url,
                                    name = name,
                                    extension = item.extension,
                                    dir = post.user.creatorId.value,
                                )
                            }
                        }
                    }

                    delay(100)
                }

                _downloadedEvent.send(PostId("-1"))

                setForegroundService(false)
            }
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
            .setSmallIcon(R.drawable.vec_app_icon_translate)
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

    inner class PostDownloadBinder : Binder() {
        fun getService(): PostDownloadService = this@PostDownloadService
    }
}
