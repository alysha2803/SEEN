package com.example.seen.ui.minigames

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.seen.data.GateState
import com.example.seen.data.MiniGame
import com.example.seen.data.gateState
import com.example.seen.state.ProgressViewModel
import com.example.seen.ui.theme.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

/**
 * Placeholder for mini-games not yet implemented (MG2–5).
 * Lets the vertical slice prove the gating loop end-to-end; replace with
 * the real composable in Milestones 6 and 8.
 */
@Composable
fun MiniGameStub(
    title: String,
    description: String,
    mg: MiniGame,
    vm: ProgressViewModel,
    onComplete: () -> Unit
) {
    val state by vm.state.collectAsState()
    val gate = mg.gateState(state.highestCompletedOrder)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge, color = Primary)
        Spacer(Modifier.height(16.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = OnDarkBackground,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(36.dp))

        // Dev shortcut: mark complete so the gating loop can be tested end-to-end
        if (gate == GateState.UNLOCKED) {
            Button(
                onClick = {
                    vm.completeMiniGame(mg)
                    onComplete()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("[DEV] Mark complete and continue")
            }
            Spacer(Modifier.height(8.dp))
        }

        OutlinedButton(
            onClick = onComplete,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Go back", color = OnDarkBackground)
        }
    }
}
