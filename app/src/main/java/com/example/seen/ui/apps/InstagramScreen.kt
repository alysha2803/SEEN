package com.example.seen.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.seen.data.GateState
import com.example.seen.data.IgPost
import com.example.seen.ui.theme.*

@Composable
fun InstagramScreen(
    handle: String,
    feed: List<IgPost>,
    gateState: GateState,
    onTriggerTap: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Profile header
        item {
            Surface(color = DarkSurface) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Primary.copy(alpha = 0.2f), RoundedCornerShape(50)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("A", fontWeight = FontWeight.Bold, color = Primary, style = MaterialTheme.typography.titleLarge)
                    }
                    Column {
                        Text("@$handle", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = OnDarkBackground)
                        Text("Public profile", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                    }
                }
            }
        }

        items(feed, key = { it.id }) { post ->
            val isAudit = post.id == "ig_audit"
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .then(if (isAudit) Modifier.clickable(onClick = onTriggerTap) else Modifier)
            ) {
                // Post header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.size(28.dp).background(Primary.copy(alpha = 0.15f), RoundedCornerShape(50)), contentAlignment = Alignment.Center) {
                        Text("A", style = MaterialTheme.typography.labelSmall, color = Primary)
                    }
                    Text("@$handle", style = MaterialTheme.typography.bodySmall, color = OnDarkBackground, fontWeight = FontWeight.SemiBold)
                    if (post.live) {
                        Surface(color = Color(0xFFE53935), shape = RoundedCornerShape(4.dp)) {
                            Text("LIVE", style = MaterialTheme.typography.labelSmall, color = Color.White, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    if (post.location != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Primary, modifier = Modifier.size(14.dp))
                            Text(post.location, style = MaterialTheme.typography.labelSmall, color = Primary)
                        }
                    }
                }

                // Post image
                AsyncImage(
                    model = "file:///android_asset/images/${post.imageKey}.png",
                    contentDescription = "Post by @$handle",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth().aspectRatio(1f)
                )

                // Caption
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    Text(post.caption, style = MaterialTheme.typography.bodySmall, color = OnDarkBackground)
                    if (isAudit) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = if (gateState == GateState.LOCKED) "Something about this post…" else "Spot every exposure in this post →",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (gateState == GateState.LOCKED) TextMuted else Primary
                        )
                    }
                }
                HorizontalDivider(color = Divider)
            }
        }
    }
}
