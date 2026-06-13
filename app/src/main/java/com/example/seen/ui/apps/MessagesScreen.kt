package com.example.seen.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.seen.data.GateState
import com.example.seen.data.Message
import com.example.seen.data.Receipt
import com.example.seen.ui.theme.*

private val BUBBLE_OWNER = Color(0xFF2A2A2A)
private val BUBBLE_AINA = Color(0xFF1A3A5C)
private val CONTACT_NAME = "A ♥"

@Composable
fun MessagesScreen(
    messages: List<Message>,
    gateState: GateState,
    onTriggerTap: () -> Unit
) {
    val listState = rememberLazyListState()

    // Auto-scroll to the bottom (most recent) on first composition
    LaunchedEffect(Unit) {
        if (messages.isNotEmpty()) listState.scrollToItem(messages.size + 1)
    }

    Column(modifier = Modifier.fillMaxSize().background(DarkBackground)) {
        // Thread header
        Surface(
            color = DarkSurface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(Primary.copy(alpha = 0.2f), RoundedCornerShape(50)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("A", fontWeight = FontWeight.Bold, color = Primary)
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(CONTACT_NAME, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = OnDarkBackground)
                    Text("Mobile", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                }
            }
        }

        // Message thread
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            itemsIndexed(messages, key = { _, m -> m.id }) { idx, msg ->
                if (msg.afterBlock && idx > 0 && !messages[idx - 1].afterBlock) {
                    BlockedDivider()
                    Spacer(Modifier.height(8.dp))
                }
                MessageBubble(msg)
            }
        }

        // MG1 trigger banner
        Surface(
            color = DarkSurface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = when (gateState) {
                        GateState.COMPLETED -> "Something felt off. You sorted it out."
                        else -> "Something feels off. Sort through this."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Button(
                    onClick = onTriggerTap,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (gateState == GateState.LOCKED) DarkSurfaceVariant else Primary
                    )
                ) {
                    Text(
                        text = when (gateState) {
                            GateState.UNLOCKED -> "Affection or red flag? →"
                            GateState.COMPLETED -> "Play again"
                            GateState.LOCKED -> "I can't make sense of this yet"
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(msg: Message) {
    val isOwner = msg.fromOwner
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isOwner) Arrangement.End else Arrangement.Start
    ) {
        Column(
            horizontalAlignment = if (isOwner) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 12.dp,
                    topEnd = 12.dp,
                    bottomStart = if (isOwner) 12.dp else 2.dp,
                    bottomEnd = if (isOwner) 2.dp else 12.dp
                ),
                color = if (isOwner) BUBBLE_OWNER else BUBBLE_AINA
            ) {
                Text(
                    text = msg.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnDarkBackground,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    text = msg.timestamp,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
                if (msg.receipt != null && isOwner) {
                    Text(
                        text = when (msg.receipt) {
                            Receipt.READ -> "Read"
                            Receipt.DELIVERED -> "Delivered"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = if (msg.receipt == Receipt.READ) Primary else TextMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun BlockedDivider() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = Divider)
        Text(
            text = "  — she blocked you —  ",
            style = MaterialTheme.typography.labelSmall,
            color = Primary.copy(alpha = 0.7f)
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = Divider)
    }
}
