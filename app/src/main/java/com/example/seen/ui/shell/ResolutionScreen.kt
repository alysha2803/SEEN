package com.example.seen.ui.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.seen.audio.LocalSoundManager
import com.example.seen.audio.SoundEffect
import com.example.seen.data.ContentRepository
import com.example.seen.ui.theme.*

@Composable
fun ResolutionScreen(onDone: () -> Unit) {
    val sound = LocalSoundManager.current
    LaunchedEffect(Unit) { sound?.play(SoundEffect.RESOLUTION_CHIME) }

    val lines = ContentRepository.resolutionLines
    var lineIndex by remember { mutableIntStateOf(0) }
    val allShown = lineIndex >= lines.size

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .then(if (!allShown) Modifier.clickable { lineIndex++ } else Modifier)
    ) {
        if (!allShown) {
            // Sequential resolution lines, one tap at a time
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = lines[lineIndex],
                    style = MaterialTheme.typography.titleMedium,
                    color = OnDarkBackground,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center
                )
            }
            Text(
                "tap to continue",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 36.dp)
            )
        } else {
            // Debrief + closing line + resources button
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    ContentRepository.debriefPrompt,
                    style = MaterialTheme.typography.titleMedium,
                    color = Primary,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(28.dp))
                Text(
                    ContentRepository.closingLine,
                    style = MaterialTheme.typography.bodyLarge,
                    color = OnDarkBackground,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(44.dp))
                Button(
                    onClick = onDone,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text("Resources & what to do")
                }
            }
        }
    }
}
