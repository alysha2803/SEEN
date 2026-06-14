package com.example.seen.ui.minigames

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.seen.audio.LocalSoundManager
import com.example.seen.audio.SoundEffect
import com.example.seen.data.ContentRepository
import com.example.seen.data.MiniGame
import com.example.seen.state.ProgressViewModel
import com.example.seen.ui.theme.*

private fun sourceLabel(postId: String): String = when (postId) {
    "ig_audit"      -> "\"saturday reset\" post — pilates + lunch"
    "ig_office"     -> "\"nasi lemak near my office\" post"
    "ig_car"        -> "\"finally got her washed\" car post"
    "ig_qr"         -> "DuitNow QR code post"
    "tw_housemate"  -> "housemate tweet on X (with her number)"
    else            -> postId
}

@Composable
fun TraceItBack(
    vm: ProgressViewModel,
    onHelp: () -> Unit,
    onComplete: () -> Unit
) {
    val sound = LocalSoundManager.current
    val facts = remember { ContentRepository.traceFacts.shuffled() }
    val allSourceIds = remember { facts.map { it.sourcePostId } }

    var currentIndex by remember { mutableIntStateOf(0) }
    var wrongFeedback by remember { mutableStateOf<String?>(null) }

    if (currentIndex >= facts.size) {
        LaunchedEffect(Unit) { sound?.play(SoundEffect.MINIGAME_COMPLETE) }
        TraceItBackSuccess(
            onDone = {
                vm.completeMiniGame(MiniGame.TRACE_IT_BACK)
                onComplete()
            }
        )
        return
    }

    val currentFact = facts[currentIndex]
    val shuffledOptions = remember(currentIndex) { allSourceIds.shuffled() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Trace it back",
                    style = MaterialTheme.typography.titleLarge,
                    color = OnDarkBackground,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "${currentIndex + 1} of ${facts.size}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
            IconButton(onClick = onHelp) {
                Icon(
                    Icons.Default.Help,
                    contentDescription = "Leave / get help",
                    tint = TextMuted
                )
            }
        }

        LinearProgressIndicator(
            progress = { currentIndex.toFloat() / facts.size },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp),
            color = Primary,
            trackColor = DarkSurfaceVariant
        )

        Spacer(Modifier.height(28.dp))

        // Fact card
        Surface(
            color = PrimaryContainer,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "He knew:",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnDarkBackground.copy(alpha = 0.6f)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    currentFact.label,
                    style = MaterialTheme.typography.titleMedium,
                    color = OnDarkBackground,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(Modifier.height(20.dp))
        Text(
            "Which post leaked this?",
            style = MaterialTheme.typography.bodyLarge,
            color = OnDarkBackground
        )
        Spacer(Modifier.height(12.dp))

        // Source options
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            shuffledOptions.forEach { sourceId ->
                OutlinedButton(
                    onClick = {
                        wrongFeedback = null
                        if (sourceId == currentFact.sourcePostId) {
                            sound?.play(SoundEffect.MINIGAME_CORRECT)
                            currentIndex++
                        } else {
                            sound?.play(SoundEffect.TENSION_STING)
                            wrongFeedback = "Not quite — look at the other options."
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = OnDarkBackground),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Divider),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        sourceLabel(sourceId),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        wrongFeedback?.let { fb ->
            Spacer(Modifier.height(12.dp))
            Text(
                fb,
                style = MaterialTheme.typography.bodySmall,
                color = Primary
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun TraceItBackSuccess(onDone: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Every piece.",
            style = MaterialTheme.typography.titleLarge,
            color = Primary,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Every single thing he knows about her, she gave him herself. Without ever knowing she did.",
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
            Text("Back to Notes")
        }
    }
}
