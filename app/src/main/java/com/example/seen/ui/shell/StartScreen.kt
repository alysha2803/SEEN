package com.example.seen.ui.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seen.audio.LocalSoundManager
import com.example.seen.ui.theme.*

@Composable
fun StartScreen(onStart: () -> Unit) {
    val sound = LocalSoundManager.current
    LaunchedEffect(Unit) { sound?.startBgMusic() }

    var showHowToPlay by remember { mutableStateOf(false) }
    var showAbout by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(Modifier.height(32.dp))

            Text(
                text = "SEEN",
                fontSize = 72.sp,
                fontWeight = FontWeight.Black,
                color = Primary,
                letterSpacing = 12.sp
            )

            Text(
                text = "a game about what stalking really looks like",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(40.dp))

            Button(
                onClick = onStart,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("Start", fontWeight = FontWeight.SemiBold)
            }

            OutlinedButton(
                onClick = { showHowToPlay = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = OnDarkBackground)
            ) {
                Text("How to Play")
            }

            TextButton(
                onClick = { showAbout = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(contentColor = TextMuted)
            ) {
                Text("About")
            }
        }
    }

    if (showHowToPlay) {
        AlertDialog(
            onDismissRequest = { showHowToPlay = false },
            containerColor = DarkSurface,
            title = {
                Text("How to Play", color = Primary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    HowToPlayItem(
                        heading = "You are looking at someone's phone.",
                        body = "Explore the apps — messages, social media, call logs, gallery — to piece together what's been happening."
                    )
                    HowToPlayItem(
                        heading = "Spot the mini-games.",
                        body = "When a button appears inside an app (e.g. \"Affection or red flag?\"), tap it to play. Mini-games test whether you can identify unhealthy behaviour."
                    )
                    HowToPlayItem(
                        heading = "One unlocks the next.",
                        body = "Completing a mini-game unlocks the next app. Work through them in order to reach the end."
                    )
                    HowToPlayItem(
                        heading = "You can leave any time.",
                        body = "Tap the ? icon in any screen to pause and access support resources. Your progress is saved."
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showHowToPlay = false }) {
                    Text("Got it", color = Primary)
                }
            }
        )
    }

    if (showAbout) {
        AlertDialog(
            onDismissRequest = { showAbout = false },
            containerColor = DarkSurface,
            title = {
                Text("About SEEN", color = Primary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "SEEN is an educational awareness game about recognising stalking behaviour.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnDarkBackground
                    )
                    HorizontalDivider(color = Divider)
                    Text(
                        text = "Disclaimer",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = Primary
                    )
                    Text(
                        text = "All characters, events, locations, and conversations in this game are entirely fictional and have been created for educational purposes only. Any resemblance to real persons, living or dead, or to actual events is purely coincidental.",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnDarkBackground
                    )
                    Text(
                        text = "All images used in this game are stock photographs or AI-generated images depicting fictional individuals. No real person is depicted without their consent.",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnDarkBackground
                    )
                    Text(
                        text = "This game is intended solely to raise awareness and build understanding of stalking and its impacts. It does not endorse, glorify, or provide instruction in any harmful behaviour.",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnDarkBackground
                    )
                    HorizontalDivider(color = Divider)
                    Text(
                        text = "Credits",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = Primary
                    )
                    Text(
                        text = "Developed by Alysha Hannani\nMatrics No. U2101207\nBuilt for WIG3005 Game Development",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnDarkBackground
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAbout = false }) {
                    Text("Close", color = Primary)
                }
            }
        )
    }
}

@Composable
private fun HowToPlayItem(heading: String, body: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = heading,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = OnDarkBackground
        )
        Text(
            text = body,
            style = MaterialTheme.typography.bodySmall,
            color = OnDarkBackground.copy(alpha = 0.75f)
        )
    }
}
