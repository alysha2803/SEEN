package com.example.seen.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.seen.data.GateState
import com.example.seen.ui.theme.*

@Composable
fun MonitorScreen(
    activity: List<String>,
    gateState: GateState,
    onTriggerTap: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505))
    ) {
        // Mock dashboard header
        Surface(color = Color(0xFF0A1A0A), modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Circle, contentDescription = null, tint = Color(0xFF00FF41), modifier = Modifier.size(10.dp))
                Text("MONITOR", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF00FF41))
                Spacer(Modifier.weight(1f))
                Text("LIVE", style = MaterialTheme.typography.labelSmall, color = Color(0xFF00FF41))
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 8.dp)
        ) {
            items(activity) { line ->
                Surface(
                    color = Color(0xFF0A0A0A),
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00FF41).copy(alpha = 0.2f))
                ) {
                    Text(
                        text = line,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF00FF41),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        // MG4 trigger
        Surface(color = Color(0xFF0A0A0A), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (gateState == GateState.COMPLETED) "You spotted how this hides." else "Could you have spotted this on her phone?",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF00FF41).copy(alpha = 0.6f)
                )
                Button(
                    onClick = onTriggerTap,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (gateState == GateState.LOCKED) Color(0xFF1A1A1A) else Color(0xFF003300)
                    )
                ) {
                    Text(
                        text = when (gateState) {
                            GateState.UNLOCKED -> "Check this device →"
                            GateState.COMPLETED -> "Play again"
                            GateState.LOCKED -> "I can't read this yet"
                        },
                        color = Color(0xFF00FF41)
                    )
                }
            }
        }
    }
}
