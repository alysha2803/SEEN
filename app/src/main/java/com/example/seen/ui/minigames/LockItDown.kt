package com.example.seen.ui.minigames

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.seen.data.AuditHotspot
import com.example.seen.data.ContentRepository
import com.example.seen.data.MiniGame
import com.example.seen.state.ProgressViewModel
import com.example.seen.ui.theme.*

@Composable
fun LockItDown(
    vm: ProgressViewModel,
    onHelp: () -> Unit,
    onComplete: () -> Unit
) {
    val hotspots = remember { ContentRepository.lockItDownHotspots.shuffled() }
    val exposingHotspots = remember { hotspots.filter { it.exposing } }
    var tapped by remember { mutableStateOf(emptySet<String>()) }
    val allExposingFound = remember(tapped) { exposingHotspots.all { it.id in tapped } }

    if (allExposingFound && exposingHotspots.isNotEmpty()) {
        LockItDownSuccess(
            onDone = {
                vm.completeMiniGame(MiniGame.LOCK_IT_DOWN)
                onComplete()
            }
        )
        return
    }

    val remaining = exposingHotspots.count { it.id !in tapped }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Box {
                AsyncImage(
                    model = "file:///android_asset/images/${ContentRepository.auditPost.imageKey}.png",
                    contentDescription = "Aina's post",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )
                IconButton(
                    onClick = onHelp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        Icons.Default.Help,
                        contentDescription = "Leave / get help",
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }

        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    "What gives her away?",
                    style = MaterialTheme.typography.titleMedium,
                    color = OnDarkBackground,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "Tap everything exposing. $remaining item${if (remaining != 1) "s" else ""} still hidden.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
                Spacer(Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = {
                        1f - (remaining.toFloat() / exposingHotspots.size.coerceAtLeast(1))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp),
                    color = Primary,
                    trackColor = DarkSurfaceVariant
                )
            }
        }

        items(hotspots, key = { it.id }) { hotspot ->
            HotspotRow(
                hotspot = hotspot,
                isTapped = hotspot.id in tapped,
                onTap = { tapped = tapped + hotspot.id }
            )
        }
    }
}

@Composable
private fun HotspotRow(
    hotspot: AuditHotspot,
    isTapped: Boolean,
    onTap: () -> Unit
) {
    val bg = when {
        !isTapped -> DarkSurface
        hotspot.exposing -> PrimaryContainer
        else -> Color(0xFF1B3A2A)
    }
    val borderColor = when {
        !isTapped -> Divider
        hotspot.exposing -> Primary
        else -> Color(0xFF52B788)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable(enabled = !isTapped, onClick = onTap),
        color = bg,
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = when {
                    !isTapped -> "?"
                    hotspot.exposing -> "⚠"
                    else -> "✓"
                },
                color = when {
                    !isTapped -> TextMuted
                    hotspot.exposing -> Primary
                    else -> Color(0xFF52B788)
                },
                style = MaterialTheme.typography.titleMedium
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = hotspot.note,
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnDarkBackground
                )
                if (isTapped) {
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = if (hotspot.exposing) "Goes on his map." else "Harmless — nothing to track.",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (hotspot.exposing) Primary else Color(0xFF52B788)
                    )
                }
            }
        }
    }
}

@Composable
private fun LockItDownSuccess(onDone: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "His map went dark.",
            style = MaterialTheme.typography.titleLarge,
            color = Primary,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(20.dp))
        Text(
            text = ContentRepository.lockItDownSuccess,
            style = MaterialTheme.typography.bodyLarge,
            color = OnDarkBackground,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(40.dp))
        Button(
            onClick = onDone,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Text("Back to Instagram")
        }
    }
}
