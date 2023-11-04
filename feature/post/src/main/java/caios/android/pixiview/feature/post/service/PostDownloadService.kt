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
import caios.android.pixiview.core.model.NotificationConfigs
import caios.android.pixiview.core.model.fanbox.FanboxPostDetail
import caios.android.pixiview.core.model.fanbox.id.PostId
import caios.android.pixiview.core.repository.FanboxRepository
import caios.android.pixiview.feature.post.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PostDownloadService : Service() {

    @Inject
    lateinit var fanboxRepository: FanboxRepository

    private val manager by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
    private val notifyConfig = NotificationConfigs.download

    private val binder = PostDownloadBinder()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _downloadedEvent = Channel<PostId>()

    val downloadedEvent = _downloadedEvent.receiveAsFlow()

    override fun onBind(intent: Intent?): IBinder = binder

    fun downloadImages(imageItems: List<FanboxPostDetail.ImageItem>) {
        scope.launch {
            createNotifyChannel()

            for (item in imageItems) {
                val name = "illust-${item.postId}-${item.id}"

                setForegroundService(true, "$name.${item.extension}")

                fanboxRepository.downloadImage(
                    url = item.originalUrl,
                    name = name,
                    extension = item.extension,
                )
            }

            _downloadedEvent.send(imageItems.last().postId)
            setForegroundService(false)
        }
    }

    fun downloadFile(fileItem: FanboxPostDetail.FileItem) {
        scope.launch {
            createNotifyChannel()

            val name = "file-${fileItem.postId}-${fileItem.id}"

            setForegroundService(true, "${name}.${fileItem.extension}")

            fanboxRepository.downloadFile(
                url = fileItem.url,
                name = name,
                extension = fileItem.extension,
            )

            _downloadedEvent.send(fileItem.postId)
            setForegroundService(false)
        }
    }

    private fun setForegroundService(isForeground: Boolean, name: String = "") {
        if (isForeground) {
            startForeground(notifyConfig.notifyId, createNotify(baseContext, name))
        } else {
            stopForeground(STOP_FOREGROUND_REMOVE)
        }
    }

    private fun createNotify(context: Context, name: String): Notification {
        return NotificationCompat.Builder(context, notifyConfig.channelId)
            .setSmallIcon(R.drawable.vec_songs_off)
            .setContentTitle(getString(R.string.post_detail_downloading))
            .setContentText(name)
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
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = channelDescription
        }

        manager.createNotificationChannel(channel)
    }

    inner class PostDownloadBinder : Binder() {
        fun getService(): PostDownloadService = this@PostDownloadService
    }
}
