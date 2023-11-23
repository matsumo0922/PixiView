package caios.android.pixiview.core.model.contract

import caios.android.pixiview.core.model.fanbox.FanboxPost
import caios.android.pixiview.core.model.fanbox.FanboxPostDetail

interface PostDownloader {
    fun onDownloadImages(imageItems: List<FanboxPostDetail.ImageItem>)
    fun onDownloadFile(fileItem: FanboxPostDetail.FileItem)
    fun onDownloadPosts(posts: List<FanboxPost>, isIgnoreFree: Boolean, isIgnoreFile: Boolean)
}
