package com.example.seen.ui.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.seen.ui.theme.DarkBackground
import com.example.seen.ui.theme.OnDarkBackground
import com.example.seen.ui.theme.Primary

@Composable
fun ContentWarning(
    onContinue: () -> Unit,
    onLeave: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Before you continue",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Primary
            )

            Text(
                text = "This game depicts stalking as a crime in order to help you recognise it and respond to it. It contains themes of surveillance, obsessive behaviour, and psychological threat.\n\nNo violence is shown on screen.",
                style = MaterialTheme.typography.bodyLarge,
                color = OnDarkBackground
            )

            Text(
                text = "A \"Leave / get help\" option is available at any time during the game.",
                style = MaterialTheme.typography.bodyMedium,
                color = OnDarkBackground.copy(alpha = 0.7f)
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("I understand — continue")
            }

            OutlinedButton(
                onClick = onLeave,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Leave / get help", color = OnDarkBackground)
            }
        }
    }
}
