package caios.android.pixiview.feature.creator.top.items

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import caios.android.pixiview.core.model.fanbox.FanboxCreatorDetail
import caios.android.pixiview.core.model.fanbox.FanboxCreatorDetail.Platform.BOOTH
import caios.android.pixiview.core.model.fanbox.FanboxCreatorDetail.Platform.FACEBOOK
import caios.android.pixiview.core.model.fanbox.FanboxCreatorDetail.Platform.FANZA
import caios.android.pixiview.core.model.fanbox.FanboxCreatorDetail.Platform.INSTAGRAM
import caios.android.pixiview.core.model.fanbox.FanboxCreatorDetail.Platform.LINE
import caios.android.pixiview.core.model.fanbox.FanboxCreatorDetail.Platform.PIXIV
import caios.android.pixiview.core.model.fanbox.FanboxCreatorDetail.Platform.TUMBLR
import caios.android.pixiview.core.model.fanbox.FanboxCreatorDetail.Platform.TWITTER
import caios.android.pixiview.core.model.fanbox.FanboxCreatorDetail.Platform.UNKNOWN
import caios.android.pixiview.core.model.fanbox.FanboxCreatorDetail.Platform.YOUTUBE
import caios.android.pixiview.core.ui.component.PixiViewTopBar
import caios.android.pixiview.core.ui.component.TagItems
import caios.android.pixiview.core.ui.extensition.FadePlaceHolder
import caios.android.pixiview.core.ui.extensition.fanboxHeader
import caios.android.pixiview.core.ui.theme.PixiViewTheme
import caios.android.pixiview.core.ui.theme.bold
import caios.android.pixiview.feature.creator.R
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreatorTopHeader(
    creatorDetail: FanboxCreatorDetail,
    onClickTerminate: () -> Unit,
    onClickMenu: () -> Unit,
    onClickLink: (String) -> Unit,
    onClickDescription: (String) -> Unit,
    onClickFollow: (String) -> Unit,
    onClickUnfollow: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isFollowed by remember(creatorDetail.isFollowed) { mutableStateOf(creatorDetail.isFollowed) }
    val tagItems = createTags(creatorDetail)

    ConstraintLayout(modifier) {
        val (topBar, header, icon, name, creatorId, links, followButton, description, tags) = createRefs()

        SubcomposeAsyncImage(
            modifier = Modifier
                .constrainAs(header) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                }
                .aspectRatio(5 / 2f),
            model = ImageRequest.Builder(LocalContext.current)
                .fanboxHeader()
                .data(creatorDetail.coverImageUrl ?: creatorDetail.user.iconUrl)
                .build(),
            loading = {
                FadePlaceHolder()
            },
            contentScale = ContentScale.Crop,
            contentDescription = null,
        )

        SubcomposeAsyncImage(
            modifier = Modifier
                .constrainAs(icon) {
                    top.linkTo(header.bottom)
                    bottom.linkTo(header.bottom)
                    start.linkTo(parent.start, 16.dp)

                    width = Dimension.value(80.dp)
                }
                .aspectRatio(1f)
                .clip(CircleShape)
                .border(
                    width = 3.dp,
                    color = MaterialTheme.colorScheme.surface,
                    shape = CircleShape,
                ),
            model = ImageRequest.Builder(LocalContext.current)
                .fanboxHeader()
                .error(R.drawable.im_default_user)
                .data(creatorDetail.user.iconUrl)
                .build(),
            loading = {
                FadePlaceHolder()
            },
            contentScale = ContentScale.Crop,
            contentDescription = null,
        )

        ProfileLinkItem(
            modifier = Modifier.constrainAs(links) {
                top.linkTo(followButton.top)
                bottom.linkTo(followButton.bottom)
                start.linkTo(icon.end, 16.dp)
                end.linkTo(followButton.start, 16.dp)

                width = Dimension.fillToConstraints
            },
            profileLinks = creatorDetail.profileLinks.toImmutableList(),
            onClickLink = onClickLink,
        )

        Text(
            modifier = Modifier.constrainAs(name) {
                top.linkTo(followButton.bottom, 8.dp)
                start.linkTo(parent.start, 16.dp)
                end.linkTo(parent.end, 16.dp)

                width = Dimension.fillToConstraints
            },
            text = creatorDetail.user.name,
            style = MaterialTheme.typography.titleLarge.bold(),
            color = MaterialTheme.colorScheme.onSurface,
        )

        Text(
            modifier = Modifier.constrainAs(creatorId) {
                top.linkTo(name.bottom, 4.dp)
                start.linkTo(parent.start, 16.dp)
                end.linkTo(parent.end, 16.dp)

                width = Dimension.fillToConstraints
            },
            text = "@${creatorDetail.creatorId}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        DescriptionItem(
            modifier = Modifier.constrainAs(description) {
                top.linkTo(creatorId.bottom, 24.dp)
                start.linkTo(parent.start, 16.dp)
                end.linkTo(parent.end, 16.dp)

                if (tagItems.isEmpty()) {
                    bottom.linkTo(parent.bottom, 16.dp)
                }

                width = Dimension.fillToConstraints
            },
            description = creatorDetail.description,
            onClickShowDescription = onClickDescription,
        )

        if (tagItems.isNotEmpty()) {
            TagItems(
                modifier = Modifier.constrainAs(tags) {
                    top.linkTo(description.bottom, 24.dp)
                    bottom.linkTo(parent.bottom, 16.dp)
                    start.linkTo(parent.start, 16.dp)
                    end.linkTo(parent.end, 16.dp)

                    width = Dimension.fillToConstraints
                },
                tags = tagItems.toImmutableList(),
                textStyle = MaterialTheme.typography.bodySmall,
                onClickTag = { /* do nothing */ },
            )
        }

        if (isFollowed) {
            OutlinedButton(
                modifier = Modifier.constrainAs(followButton) {
                    top.linkTo(header.bottom, 8.dp)
                    end.linkTo(parent.end, 16.dp)

                    width = Dimension.value(128.dp)
                },
                onClick = {
                    isFollowed = false
                    onClickUnfollow.invoke(creatorDetail.user.userId)
                },
            ) {
                Text(text = stringResource(R.string.common_unfollow))
            }
        } else {
            Button(
                modifier = Modifier.constrainAs(followButton) {
                    top.linkTo(header.bottom, 8.dp)
                    end.linkTo(parent.end, 16.dp)

                    width = Dimension.value(128.dp)
                },
                onClick = {
                    isFollowed = true
                    onClickFollow.invoke(creatorDetail.user.userId)
                },
            ) {
                Text(text = stringResource(R.string.common_follow))
            }
        }

        PixiViewTopBar(
            modifier = Modifier
                .constrainAs(topBar) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                }
                .statusBarsPadding(),
            isTransparent = true,
            windowInsets = WindowInsets(0, 0, 0, 0),
            onClickNavigation = onClickTerminate,
            onClickActions = onClickMenu,
        )
    }
}

@Composable
private fun ProfileLinkItem(
    profileLinks: ImmutableList<FanboxCreatorDetail.ProfileLink>,
    onClickLink: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            space = 8.dp,
            alignment = Alignment.End,
        ),
    ) {
        for (profileLink in profileLinks) {
            IconButton(
                modifier = Modifier.size(32.dp),
                onClick = { onClickLink.invoke(profileLink.url) },
            ) {
                Icon(
                    modifier = Modifier.padding(4.dp),
                    painter = painterResource(
                        when (profileLink.link) {
                            BOOTH -> R.drawable.vec_booth
                            FACEBOOK -> R.drawable.vec_facebook
                            FANZA -> R.drawable.vec_fanza
                            INSTAGRAM -> R.drawable.vec_instagram
                            LINE -> R.drawable.vec_line
                            PIXIV -> R.drawable.vec_pixiv
                            TUMBLR -> R.drawable.vec_tumblr
                            TWITTER -> R.drawable.vec_twitter
                            YOUTUBE -> R.drawable.vec_youtube
                            UNKNOWN -> R.drawable.vec_unknown_link
                        },
                    ),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
private fun DescriptionItem(
    description: String,
    onClickShowDescription: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isEllipsized by remember(description) { mutableStateOf(false) }

    Row(modifier) {
        Text(
            modifier = Modifier.weight(1f),
            text = description.replace("\n", " "),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = {
                isEllipsized = it.hasVisualOverflow
            },
        )

        if (isEllipsized) {
            IconButton(onClick = { onClickShowDescription.invoke(description) }) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
private fun createTags(creatorDetail: FanboxCreatorDetail): List<String> {
    return mutableListOf<String>().apply {
        if (creatorDetail.isSupported) {
            add(stringResource(R.string.creator_tag_is_supported))
        }

        if (creatorDetail.hasAdultContent) {
            add(stringResource(R.string.creator_tag_has_adult_content))
        }

        if (creatorDetail.isAcceptingRequest) {
            add(stringResource(R.string.creator_tag_is_accepting_request))
        }

        if (creatorDetail.hasBoothShop) {
            add(stringResource(R.string.creator_tag_has_booth_shop))
        }
    }
}

@Preview
@Composable
private fun CreatorTopHeaderPreview() {
    PixiViewTheme {
        CreatorTopHeader(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxWidth(),
            creatorDetail = FanboxCreatorDetail.dummy(),
            onClickTerminate = {},
            onClickMenu = {},
            onClickLink = {},
            onClickDescription = {},
            onClickFollow = {},
            onClickUnfollow = {},
        )
    }
}
