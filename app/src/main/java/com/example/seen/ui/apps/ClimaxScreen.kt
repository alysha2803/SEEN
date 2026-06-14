package com.example.seen.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.seen.data.ContentRepository
import com.example.seen.data.GateState
import com.example.seen.ui.theme.*

@Composable
fun ClimaxScreen(
    gateState: GateState,
    onTriggerTap: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(4.dp))

        // The notification echoing from Scene 0, now understood in its true context
        Surface(
            color = DarkSurface,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    "Maps",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
                Text(
                    ContentRepository.Lockscreen.arrivedHomeNotification,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Text(
            "That wasn't a sweet notification. It meant he knew she was home — because he's been watching her all along.",
            style = MaterialTheme.typography.bodyLarge,
            color = OnDarkBackground
        )

        Text(
            "She's there now. Alone. The grey app confirms it, live. " +
                "His last note, half-written: \"tonight I'll finally talk to her. she'll understand.\" " +
                "His location is already moving toward hers.",
            style = MaterialTheme.typography.bodyMedium,
            color = OnDarkBackground.copy(alpha = 0.85f)
        )

        Text(
            "You have her number from the X post. You have her address. The phone in your hand has everything.",
            style = MaterialTheme.typography.bodyMedium,
            color = OnDarkBackground.copy(alpha = 0.85f),
            fontStyle = FontStyle.Italic
        )

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = onTriggerTap,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = when (gateState) {
                    GateState.UNLOCKED  -> Primary
                    GateState.COMPLETED -> DarkSurface
                    GateState.LOCKED    -> DarkSurfaceVariant
                }
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                text = when (gateState) {
                    GateState.UNLOCKED  -> "What do you do? →"
                    GateState.COMPLETED -> "You got her out."
                    GateState.LOCKED    -> "I can't focus on this yet"
                },
                color = when (gateState) {
                    GateState.UNLOCKED  -> OnDarkBackground
                    else                -> TextMuted
                },
                fontWeight = if (gateState == GateState.UNLOCKED) FontWeight.SemiBold else FontWeight.Normal
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}
