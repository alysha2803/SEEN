package com.example.seen.ui.minigames

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import com.example.seen.data.BehaviorCard
import com.example.seen.data.ContentRepository
import com.example.seen.data.MiniGame
import com.example.seen.state.ProgressViewModel
import com.example.seen.ui.theme.*
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

private const val SWIPE_THRESHOLD = 180f

@Composable
fun AffectionSwipe(vm: ProgressViewModel, onHelp: () -> Unit = {}, onComplete: () -> Unit) {
    val deck = remember { ContentRepository.affectionDeck }
    var currentIndex by remember { mutableIntStateOf(0) }
    var redFlagsCorrect by remember { mutableIntStateOf(0) }

    val totalRedFlags = remember { deck.count { it.isRedFlag } }
    val isDone = currentIndex >= deck.size

    if (isDone) {
        AffectionSummary(
            redFlagsFound = redFlagsCorrect,
            totalRedFlags = totalRedFlags,
            onDone = {
                vm.completeMiniGame(MiniGame.AFFECTION_OR_RED_FLAG)
                onComplete()
            }
        )
        return
    }

    val card = deck[currentIndex]

    // playerSaidRedFlag: true = player chose "warning", false = "sweet"
    fun onDecision(playerSaidRedFlag: Boolean) {
        if (card.isRedFlag && playerSaidRedFlag) redFlagsCorrect++
        currentIndex++
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        // Tone indicator
        val tension = (currentIndex.toFloat() / deck.size).coerceIn(0f, 1f)
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Affection or red flag?",
                        style = MaterialTheme.typography.titleLarge,
                        color = OnDarkBackground,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${currentIndex + 1} / ${deck.size}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
                IconButton(onClick = onHelp, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Help, contentDescription = "Leave / get help", tint = TextMuted)
                }
            }
            Spacer(Modifier.height(8.dp))

            // Tone bar
            ToneBar(tension = tension)

            Spacer(Modifier.height(24.dp))

            // Swipeable card
            SwipeCard(
                card = card,
                modifier = Modifier.weight(1f),
                onSwipeLeft = { onDecision(true) },   // left = warning sign
                onSwipeRight = { onDecision(false) }  // right = sweet
            )

            Spacer(Modifier.height(16.dp))

            // Fallback buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { onDecision(true) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary)
                ) {
                    Text("⚠ Warning", fontSize = 13.sp)
                }
                Button(
                    onClick = { onDecision(false) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4332))
                ) {
                    Text("♥ Sweet", fontSize = 13.sp, color = Color(0xFF52B788))
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(
                text = "← swipe left for warning · right for sweet →",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SwipeCard(
    card: BehaviorCard,
    modifier: Modifier = Modifier,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit
) {
    val scope = rememberCoroutineScope()
    // Keyed to card so it resets between cards
    val offsetX = remember(card) { Animatable(0f) }

    val clampedOffset = offsetX.value.coerceIn(-SWIPE_THRESHOLD * 1.5f, SWIPE_THRESHOLD * 1.5f)
    val progress = (clampedOffset / SWIPE_THRESHOLD).coerceIn(-1f, 1f)

    // Card color: green when dragging right (sweet), red when left (warning)
    val cardColor = when {
        progress > 0.05f -> lerp(DarkSurface, Color(0xFF1B4332), progress)
        progress < -0.05f -> lerp(DarkSurface, Color(0xFF3D0010), -progress)
        else -> DarkSurface
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        // Hint labels (revealed as card moves)
        Row(
            modifier = Modifier.fillMaxWidth().align(Alignment.Center),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "⚠ Warning",
                color = Color(0xFFCF6679).copy(alpha = (-progress).coerceIn(0f, 1f)),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                "♥ Sweet",
                color = Color(0xFF52B788).copy(alpha = progress.coerceIn(0f, 1f)),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Surface(
            modifier = Modifier
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .rotate(progress * 8f)
                .fillMaxWidth(0.88f)
                .aspectRatio(0.75f)
                .pointerInput(card) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            scope.launch {
                                when {
                                    offsetX.value > SWIPE_THRESHOLD -> {
                                        offsetX.animateTo(800f, tween(200))
                                        onSwipeRight()
                                    }
                                    offsetX.value < -SWIPE_THRESHOLD -> {
                                        offsetX.animateTo(-800f, tween(200))
                                        onSwipeLeft()
                                    }
                                    else -> offsetX.animateTo(0f, tween(250))
                                }
                            }
                        },
                        onHorizontalDrag = { _, delta ->
                            scope.launch { offsetX.snapTo(offsetX.value + delta) }
                        }
                    )
                },
            shape = RoundedCornerShape(16.dp),
            color = cardColor,
            tonalElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = card.text,
                    style = MaterialTheme.typography.titleLarge,
                    color = OnDarkBackground,
                    textAlign = TextAlign.Center,
                    lineHeight = 32.sp
                )
            }
        }
    }
}

@Composable
private fun ToneBar(tension: Float) {
    val barColor = lerp(Color(0xFF52B788), Primary, tension)
    val label = when {
        tension < 0.2f -> "Harmless..."
        tension < 0.5f -> "Something feels off"
        tension < 0.75f -> "This isn't devotion"
        else -> "This is control"
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = barColor, fontStyle = FontStyle.Italic)
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { tension },
            modifier = Modifier.fillMaxWidth().height(4.dp),
            color = barColor,
            trackColor = DarkSurfaceVariant
        )
    }
}

@Composable
private fun AffectionSummary(
    redFlagsFound: Int,
    totalRedFlags: Int,
    onDone: () -> Unit
) {
    val allFound = redFlagsFound >= totalRedFlags

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("You spotted", style = MaterialTheme.typography.bodyLarge, color = TextMuted)
        Spacer(Modifier.height(8.dp))
        Text(
            text = "$redFlagsFound of $totalRedFlags",
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 48.sp),
            color = Primary,
            fontWeight = FontWeight.Bold
        )
        Text("warning signs", style = MaterialTheme.typography.bodyLarge, color = TextMuted)
        Spacer(Modifier.height(28.dp))
        Text(
            text = if (allFound)
                "Every single one. This wasn't devotion — it was control wearing devotion's clothes."
            else
                "The ones that look like care are the hardest to name. That's exactly how this works.",
            style = MaterialTheme.typography.bodyLarge,
            color = OnDarkBackground,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(36.dp))
        Button(
            onClick = onDone,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Text("Back to messages")
        }
    }
}
