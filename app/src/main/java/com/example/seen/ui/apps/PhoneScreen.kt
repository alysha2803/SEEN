package com.example.seen.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallMissed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.seen.data.CallLogEntry
import com.example.seen.data.Conversation
import com.example.seen.ui.theme.*

@Composable
fun PhoneScreen(
    contactName: String,
    summary: String,
    callLog: List<CallLogEntry>,
    conversations: List<Conversation>
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Call Logs", "Contacts")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Contact card — always visible
        Surface(color = DarkSurface, modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(Primary.copy(alpha = 0.15f), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "A",
                        style = MaterialTheme.typography.titleLarge,
                        color = Primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(contactName, style = MaterialTheme.typography.titleLarge, color = OnDarkBackground, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Text(summary, style = MaterialTheme.typography.bodySmall, color = Primary)
            }
        }

        // Tabs
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = DarkSurface,
            contentColor = Primary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            title,
                            color = if (selectedTabIndex == index) Primary else TextMuted
                        )
                    }
                )
            }
        }

        when (selectedTabIndex) {
            0 -> CallLogTab(callLog)
            1 -> ContactsTab(conversations)
        }
    }
}

@Composable
private fun CallLogTab(callLog: List<CallLogEntry>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
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
                Icon(
                    Icons.Default.CallMissed,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(20.dp)
                )
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

@Composable
private fun ContactsTab(conversations: List<Conversation>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(conversations, key = { it.contactId }) { conv ->
            val initial = conv.displayName.firstOrNull { it.isLetter() }?.uppercaseChar()?.toString() ?: "?"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Primary.copy(alpha = 0.15f), RoundedCornerShape(50)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        initial,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    conv.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnDarkBackground
                )
            }
            HorizontalDivider(color = Divider)
        }
    }
}
