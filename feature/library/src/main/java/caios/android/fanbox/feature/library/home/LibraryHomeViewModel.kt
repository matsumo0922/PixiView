package caios.android.fanbox.feature.library.home

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import caios.android.fanbox.core.common.PixiViewConfig
import caios.android.fanbox.core.common.util.suspendRunCatching
import caios.android.fanbox.core.model.UserData
import caios.android.fanbox.core.model.fanbox.FanboxPost
import caios.android.fanbox.core.model.fanbox.id.PostId
import caios.android.fanbox.core.repository.FanboxRepository
import caios.android.fanbox.core.repository.UserDataRepository
import caios.android.fanbox.core.ui.ads.NativeAdsPreLoader
import caios.android.fanbox.core.ui.extensition.emptyPaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryHomeViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val fanboxRepository: FanboxRepository,
    private val nativeAdsPreLoader: NativeAdsPreLoader,
    private val pixiViewConfig: PixiViewConfig,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        LibraryUiState(
            userData = UserData.dummy(),
            bookmarkedPosts = emptyList(),
            homePaging = emptyPaging(),
            supportedPaging = emptyPaging(),
            nativeAdUnitId = pixiViewConfig.adMobNativeAdUnitId,
        ),
    )

    val uiState = _uiState.asStateFlow()

    val adsPreLoader = nativeAdsPreLoader

    init {
        viewModelScope.launch {
            userDataRepository.userData.collectLatest { userData ->
                val loadSize = if (userData.isHideRestricted || userData.isGridMode) 20 else 10
                val isHideRestricted = userData.isHideRestricted

                _uiState.value = uiState.value.copy(
                    userData = userData,
                    homePaging = fanboxRepository.getHomePostsPager(loadSize, isHideRestricted),
                    supportedPaging = fanboxRepository.getSupportedPostsPager(loadSize, isHideRestricted),
                    nativeAdUnitId = pixiViewConfig.adMobNativeAdUnitId,
                )
            }
        }

        viewModelScope.launch {
            fanboxRepository.bookmarkedPosts.collectLatest {
                _uiState.value = uiState.value.copy(bookmarkedPosts = it)
            }
        }

        adsPreLoader.preloadAd()
    }

    fun postLike(postId: PostId) {
        viewModelScope.launch {
            suspendRunCatching {
                fanboxRepository.likePost(postId)
            }
        }
    }

    fun postBookmark(post: FanboxPost, isBookmarked: Boolean) {
        viewModelScope.launch {
            suspendRunCatching {
                if (isBookmarked) {
                    fanboxRepository.bookmarkPost(post)
                } else {
                    fanboxRepository.unbookmarkPost(post)
                }
            }
        }
    }
}

@Stable
data class LibraryUiState(
    val userData: UserData,
    val bookmarkedPosts: List<PostId>,
    val homePaging: Flow<PagingData<FanboxPost>>,
    val supportedPaging: Flow<PagingData<FanboxPost>>,
    val nativeAdUnitId: String,
)
