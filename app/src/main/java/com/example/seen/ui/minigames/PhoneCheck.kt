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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.seen.audio.LocalSoundManager
import com.example.seen.audio.SoundEffect
import com.example.seen.data.ContentRepository
import com.example.seen.data.DeviceSignal
import com.example.seen.data.MiniGame
import com.example.seen.state.ProgressViewModel
import com.example.seen.ui.theme.*

private val MonitorGreen = Color(0xFF00FF41)

@Composable
fun PhoneCheck(
    vm: ProgressViewModel,
    onHelp: () -> Unit,
    onComplete: () -> Unit
) {
    val sound = LocalSoundManager.current
    val signals = remember { ContentRepository.deviceSignals.shuffled() }
    val suspiciousSignals = remember { signals.filter { it.isSuspicious } }
    var flagged by remember { mutableStateOf(emptySet<String>()) }
    val allFlagged = remember(flagged) { suspiciousSignals.all { it.label in flagged } }

    if (allFlagged && suspiciousSignals.isNotEmpty()) {
        LaunchedEffect(Unit) { sound?.play(SoundEffect.MINIGAME_COMPLETE) }
        PhoneCheckSuccess(
            onDone = {
                vm.completeMiniGame(MiniGame.IS_THIS_PHONE_CLEAN)
                onComplete()
            }
        )
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505)),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Is this phone clean?",
                        style = MaterialTheme.typography.titleLarge,
                        color = MonitorGreen,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "This is what was hiding on Aina's phone.\nCould you have spotted it?",
                        style = MaterialTheme.typography.bodySmall,
                        color = MonitorGreen.copy(alpha = 0.6f)
                    )
                }
                IconButton(onClick = onHelp) {
                    Icon(
                        Icons.Default.Help,
                        contentDescription = "Leave / get help",
                        tint = MonitorGreen.copy(alpha = 0.4f)
                    )
                }
            }
            Text(
                "Tap anything suspicious.",
                style = MaterialTheme.typography.labelSmall,
                color = MonitorGreen.copy(alpha = 0.4f),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        items(signals, key = { it.label }) { signal ->
            SignalRow(
                signal = signal,
                isFlagged = signal.label in flagged,
                onFlag = {
                    sound?.play(SoundEffect.UI_TAP)
                    flagged = flagged + signal.label
                }
            )
        }
    }
}

@Composable
private fun SignalRow(
    signal: DeviceSignal,
    isFlagged: Boolean,
    onFlag: () -> Unit
) {
    val borderColor = when {
        !isFlagged         -> MonitorGreen.copy(alpha = 0.15f)
        signal.isSuspicious -> Primary
        else               -> MonitorGreen.copy(alpha = 0.5f)
    }
    val bg = when {
        !isFlagged         -> Color(0xFF0A0A0A)
        signal.isSuspicious -> Color(0xFF2D0A10)
        else               -> Color(0xFF0A1A0A)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable(enabled = !isFlagged, onClick = onFlag),
        color = bg,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = when {
                    !isFlagged         -> "○"
                    signal.isSuspicious -> "⚠"
                    else               -> "✓"
                },
                color = when {
                    !isFlagged         -> MonitorGreen.copy(alpha = 0.4f)
                    signal.isSuspicious -> Primary
                    else               -> MonitorGreen
                },
                style = MaterialTheme.typography.bodyLarge
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = signal.label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isFlagged && signal.isSuspicious) OnDarkBackground else MonitorGreen.copy(alpha = 0.9f),
                    fontWeight = if (isFlagged && signal.isSuspicious) FontWeight.SemiBold else FontWeight.Normal
                )
                if (isFlagged) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = signal.why,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (signal.isSuspicious) Primary else MonitorGreen.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
private fun PhoneCheckSuccess(onDone: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505))
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "You'd have seen it.",
            style = MaterialTheme.typography.titleLarge,
            color = MonitorGreen,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "An app with no name. Permissions no legitimate app needs. Battery it shouldn't burn. " +
                "The signs were there — if you knew what to look for.",
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
            Text("Back to Monitor")
        }
    }
}
