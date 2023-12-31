package caios.android.fanbox.feature.post.detail

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import caios.android.fanbox.core.common.PixiViewConfig
import caios.android.fanbox.core.model.fanbox.FanboxPost
import caios.android.fanbox.core.repository.FanboxRepository
import caios.android.fanbox.core.repository.UserDataRepository
import caios.android.fanbox.core.ui.ads.NativeAdsPreLoader
import caios.android.fanbox.core.ui.extensition.emptyPaging
import caios.android.fanbox.feature.post.detail.PostDetailPagingType.Creator
import caios.android.fanbox.feature.post.detail.PostDetailPagingType.Home
import caios.android.fanbox.feature.post.detail.PostDetailPagingType.Search
import caios.android.fanbox.feature.post.detail.PostDetailPagingType.Supported
import caios.android.fanbox.feature.post.detail.PostDetailPagingType.Unknown
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailRootViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val fanboxRepository: FanboxRepository,
    private val pixiViewConfig: PixiViewConfig,
    private val nativeAdsPreLoader: NativeAdsPreLoader,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        PostDetailRootUiState(
            paging = null,
            nativeAdUnitId = pixiViewConfig.adMobNativeAdUnitId,
        ),
    )

    val uiState = _uiState.asStateFlow()

    val adsPreLoader = nativeAdsPreLoader

    init {
        viewModelScope.launch {
            adsPreLoader.preloadAd()
        }
    }

    fun fetch(type: PostDetailPagingType) {
        viewModelScope.launch {
            val userData = userDataRepository.userData.first()
            val loadSize = if (userData.isHideRestricted || userData.isGridMode) 20 else 10
            val isHideRestricted = userData.isHideRestricted

            _uiState.value = PostDetailRootUiState(
                paging = when (type) {
                    Home -> fanboxRepository.getHomePostsPagerCache(loadSize, isHideRestricted)
                    Supported -> fanboxRepository.getSupportedPostsPagerCache(loadSize, isHideRestricted)
                    Creator -> fanboxRepository.getCreatorPostsPagerCache()
                    Search -> fanboxRepository.getPostsFromQueryPagerCache()
                    Unknown -> emptyPaging()
                },
                nativeAdUnitId = pixiViewConfig.adMobNativeAdUnitId,
            )
        }
    }
}

@Stable
data class PostDetailRootUiState(
    val paging: Flow<PagingData<FanboxPost>>?,
    val nativeAdUnitId: String,
)
