package caios.android.fanbox.feature.about.versions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import caios.android.fanbox.core.model.Version
import caios.android.fanbox.core.ui.AsyncLoadContents
import caios.android.fanbox.core.ui.theme.end
import caios.android.fanbox.core.ui.theme.start
import caios.android.fanbox.feature.about.R
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import java.time.format.DateTimeFormatter

@Composable
internal fun VersionHistoryRoute(
    terminate: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VersionHistoryViewModel = hiltViewModel(),
) {
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()

    AsyncLoadContents(
        modifier = modifier,
        screenState = screenState,
        retryAction = terminate,
    ) { uiState ->
        VersionHistoryDialog(
            modifier = Modifier.fillMaxWidth(),
            versions = uiState.versions.toImmutableList(),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VersionHistoryDialog(
    versions: ImmutableList<Version>,
    modifier: Modifier = Modifier,
) {
    val state = rememberTopAppBarState()
    val behavior = TopAppBarDefaults.pinnedScrollBehavior(state)

    Scaffold(
        modifier = modifier.nestedScroll(behavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    Text(
                        text = stringResource(R.string.about_support_version_history),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                },
                scrollBehavior = behavior,
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = paddingValues,
        ) {
            items(
                items = versions,
                key = { item -> item.code },
            ) {
                VersionItem(
                    modifier = Modifier.fillMaxWidth(),
                    version = it,
                )
            }
        }
    }
}

@Composable
private fun VersionItem(
    version: Version,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = "${version.name} [${version.code}]",
                    style = MaterialTheme.typography.bodyMedium.start(),
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Text(
                    modifier = Modifier.weight(1f),
                    text = version.date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
                    style = MaterialTheme.typography.bodyMedium.end(),
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = version.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Divider()
    }
}
