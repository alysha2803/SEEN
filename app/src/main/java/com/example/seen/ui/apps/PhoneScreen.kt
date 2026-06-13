package com.example.seen.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallMissed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.seen.data.CallLogEntry
import com.example.seen.ui.theme.*

@Composable
fun PhoneScreen(
    contactName: String,
    summary: String,
    callLog: List<CallLogEntry>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Contact card
        Surface(color = DarkSurface, modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(Primary.copy(alpha = 0.15f), shape = androidx.compose.foundation.shape.CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("A", style = MaterialTheme.typography.titleLarge, color = Primary, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(8.dp))
                Text(contactName, style = MaterialTheme.typography.titleLarge, color = OnDarkBackground, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Text(summary, style = MaterialTheme.typography.bodySmall, color = Primary)
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(callLog) { entry ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.CallMissed, contentDescription = null, tint = Primary, modifier = Modifier.size(20.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(entry.label, style = MaterialTheme.typography.bodyMedium, color = OnDarkBackground)
                        Text(entry.outcome, style = MaterialTheme.typography.bodySmall, color = TextMuted)
                    }
                    Text(entry.time, style = MaterialTheme.typography.bodySmall, color = TextMuted)
                }
                HorizontalDivider(color = Divider)
            }
        }
    }
}
