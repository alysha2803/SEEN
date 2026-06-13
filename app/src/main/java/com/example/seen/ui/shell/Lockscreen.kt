package com.example.seen.ui.shell

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.Help
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.seen.data.ContentRepository
import com.example.seen.ui.theme.DarkBackground
import com.example.seen.ui.theme.DarkSurface
import com.example.seen.ui.theme.OnDarkBackground
import com.example.seen.ui.theme.Primary

private enum class LockPhase { PRE_MONO, NOTIF, POST_MONO, INTERACTIVE }

@Composable
fun Lockscreen(
    onSwipeUp: () -> Unit,
    onHelp: () -> Unit
) {
    val preLines = ContentRepository.Lockscreen.preNotificationLines
    val postLines = ContentRepository.Lockscreen.postNotificationLines

    var phase by remember { mutableStateOf(LockPhase.PRE_MONO) }
    var captionIndex by remember { mutableIntStateOf(0) }

    fun advance() {
        when (phase) {
            LockPhase.PRE_MONO -> {
                if (captionIndex < preLines.lastIndex) captionIndex++
                else { captionIndex = 0; phase = LockPhase.NOTIF }
            }
            LockPhase.NOTIF -> phase = LockPhase.POST_MONO
            LockPhase.POST_MONO -> {
                if (captionIndex < postLines.lastIndex) captionIndex++
                else phase = LockPhase.INTERACTIVE
            }
            LockPhase.INTERACTIVE -> {}
        }
    }

    val currentCaption: String? = when (phase) {
        LockPhase.PRE_MONO -> preLines.getOrNull(captionIndex)
        LockPhase.POST_MONO -> postLines.getOrNull(captionIndex)
        else -> null
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .clickable(enabled = phase != LockPhase.INTERACTIVE) { advance() }
            .pointerInput(phase) {
                if (phase == LockPhase.INTERACTIVE) {
                    var totalDelta = 0f
                    detectVerticalDragGestures(
                        onDragEnd = { if (totalDelta < -80f) onSwipeUp(); totalDelta = 0f },
                        onDragCancel = { totalDelta = 0f },
                        onVerticalDrag = { _, delta -> totalDelta += delta }
                    )
                }
            }
    ) {
        // Wallpaper
        AsyncImage(
            model = "file:///android_asset/images/wallpaper_lockscreen.png",
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            alpha = 0.55f
        )

        // Help button (always accessible)
        IconButton(
            onClick = onHelp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .statusBarsPadding()
        ) {
            Icon(Icons.Default.Help, contentDescription = "Leave / get help", tint = OnDarkBackground)
        }

        // Status bar (fake phone chrome)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = ContentRepository.Lockscreen.time,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = OnDarkBackground
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = ContentRepository.Lockscreen.battery,
                    fontSize = 12.sp,
                    color = Primary
                )
                Icon(
                    Icons.Default.BatteryAlert,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Notification (visible in NOTIF phase and after)
        AnimatedVisibility(
            visible = phase == LockPhase.NOTIF || phase == LockPhase.POST_MONO || phase == LockPhase.INTERACTIVE,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp, start = 16.dp, end = 16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkSurface.copy(alpha = 0.9f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "Maps",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnDarkBackground.copy(alpha = 0.6f)
                    )
                    Text(
                        ContentRepository.Lockscreen.arrivedHomeNotification,
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnDarkBackground
                    )
                }
            }
        }

        // Caption / swipe hint at bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.6f))
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            when (phase) {
                LockPhase.INTERACTIVE -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text("↑", fontSize = 24.sp, color = OnDarkBackground)
                        Text("swipe up", style = MaterialTheme.typography.bodySmall, color = OnDarkBackground.copy(alpha = 0.6f))
                    }
                }
                else -> {
                    if (currentCaption != null) {
                        Text(
                            text = currentCaption,
                            style = MaterialTheme.typography.bodyLarge,
                            color = OnDarkBackground,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
            }
        }
    }
}
