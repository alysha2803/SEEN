package com.example.seen.ui.apps

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.seen.data.ContentRepository
import com.example.seen.data.GateState
import com.example.seen.data.IgPost
import com.example.seen.ui.theme.*

@Composable
fun InstagramScreen(
    handle: String,
    feed: List<IgPost>,
    gateState: GateState,
    onTriggerTap: () -> Unit,
    onSetBackOverride: ((() -> Unit)?) -> Unit
) {
    var selectedPostId by remember { mutableStateOf<String?>(null) }

    // Keep AppScaffold's TopAppBar back arrow in sync with internal state.
    if (selectedPostId != null) {
        DisposableEffect(Unit) {
            onSetBackOverride { selectedPostId = null }
            onDispose { onSetBackOverride(null) }
        }
    }

    if (selectedPostId != null) {
        val post = feed.find { it.id == selectedPostId }
        if (post != null) {
            BackHandler { selectedPostId = null }
            FullPostView(handle = handle, post = post)
        }
    } else {
        ProfileGridView(
            handle = handle,
            feed = feed,
            gateState = gateState,
            onTriggerTap = onTriggerTap,
            onPostTap = { selectedPostId = it }
        )
    }
}

// ---- Profile + grid ----

@Composable
private fun ProfileGridView(
    handle: String,
    feed: List<IgPost>,
    gateState: GateState,
    onTriggerTap: () -> Unit,
    onPostTap: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxSize().background(DarkBackground),
        horizontalArrangement = Arrangement.spacedBy(1.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Full-width profile header
        item(span = { GridItemSpan(maxLineSpan) }) {
            ProfileHeader(
                handle = handle,
                postCount = feed.size,
                gateState = gateState,
                onTriggerTap = onTriggerTap
            )
        }

        // Thumbnail grid
        items(feed, key = { it.id }) { post ->
            AsyncImage(
                model = "file:///android_asset/images/${post.imageKey}.png",
                contentDescription = "Post by @$handle",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .aspectRatio(1f)
                    .clickable { onPostTap(post.id) }
            )
        }
    }
}

@Composable
private fun ProfileHeader(
    handle: String,
    postCount: Int,
    gateState: GateState,
    onTriggerTap: () -> Unit
) {
    Surface(color = DarkSurface) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Primary.copy(alpha = 0.2f), RoundedCornerShape(50)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "A",
                        fontWeight = FontWeight.Bold,
                        color = Primary,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                Column {
                    Text(
                        "@$handle",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = OnDarkBackground
                    )
                    Text(
                        "$postCount posts · Public profile",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // MG2 affordance — profile-level, not per-post
            Button(
                onClick = onTriggerTap,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (gateState == GateState.LOCKED) DarkSurfaceVariant else Primary
                )
            ) {
                Text(
                    text = when (gateState) {
                        GateState.UNLOCKED  -> "Lock it down →"
                        GateState.COMPLETED -> "Play again"
                        GateState.LOCKED    -> ContentRepository.lockedNudge
                    }
                )
            }
        }
    }
}

// ---- Full post view ----

@Composable
private fun FullPostView(handle: String, post: IgPost) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            // Post header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(Primary.copy(alpha = 0.15f), RoundedCornerShape(50)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("A", style = MaterialTheme.typography.labelSmall, color = Primary)
                }
                Text(
                    "@$handle",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnDarkBackground,
                    fontWeight = FontWeight.SemiBold
                )
                if (post.live) {
                    Surface(color = Color(0xFFE53935), shape = RoundedCornerShape(4.dp)) {
                        Text(
                            "LIVE",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(Modifier.weight(1f))
                if (post.location != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(post.location, style = MaterialTheme.typography.labelSmall, color = Primary)
                    }
                }
            }
        }

        item {
            AsyncImage(
                model = "file:///android_asset/images/${post.imageKey}.png",
                contentDescription = "Post by @$handle",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )
        }

        item {
            Text(
                text = post.caption,
                style = MaterialTheme.typography.bodySmall,
                color = OnDarkBackground,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
    }
}
