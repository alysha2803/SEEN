package com.example.seen.ui.apps

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.seen.data.Conversation
import com.example.seen.data.GateState
import com.example.seen.data.Message
import com.example.seen.data.Receipt
import com.example.seen.ui.theme.*

private val BUBBLE_OWNER = Color(0xFF2A2A2A)
private val BUBBLE_AINA  = Color(0xFF1A3A5C)

@Composable
fun MessagesScreen(
    conversations: List<Conversation>,
    gateState: GateState,
    onTriggerTap: () -> Unit,
    onConversationOpened: (String) -> Unit,
    onSetBackOverride: ((() -> Unit)?) -> Unit
) {
    var selectedConversation by remember { mutableStateOf<Conversation?>(null) }

    // Keep AppScaffold's TopAppBar back arrow in sync with internal state.
    // DisposableEffect self-clears when the conversation is dismissed.
    if (selectedConversation != null) {
        DisposableEffect(Unit) {
            onSetBackOverride { selectedConversation = null }
            onDispose { onSetBackOverride(null) }
        }
    }

    if (selectedConversation == null) {
        ConversationInbox(
            conversations = conversations,
            onConversationSelected = { conv ->
                selectedConversation = conv
                onConversationOpened(conv.contactId)
            }
        )
    } else {
        val conv = selectedConversation!!
        BackHandler { selectedConversation = null }
        ConversationThread(
            conversation = conv,
            gateState = if (conv.isAina) gateState else null,
            onTriggerTap = onTriggerTap
        )
    }
}

// ---- Inbox ----

@Composable
private fun ConversationInbox(
    conversations: List<Conversation>,
    onConversationSelected: (Conversation) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        itemsIndexed(conversations, key = { _, c -> c.contactId }) { _, conv ->
            ConversationRow(conv, onClick = { onConversationSelected(conv) })
            HorizontalDivider(
                color = Divider,
                modifier = Modifier.padding(start = 72.dp)
            )
        }
    }
}

@Composable
private fun ConversationRow(conv: Conversation, onClick: () -> Unit) {
    val lastMsg = conv.messages.lastOrNull()
    val initial = conv.displayName.firstOrNull { it.isLetter() }?.uppercaseChar()?.toString() ?: "?"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Primary.copy(alpha = 0.15f), RoundedCornerShape(50)),
            contentAlignment = Alignment.Center
        ) {
            Text(initial, style = MaterialTheme.typography.bodyLarge, color = Primary, fontWeight = FontWeight.Bold)
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = conv.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = OnDarkBackground
                )
                if (lastMsg != null) {
                    Text(
                        text = lastMsg.timestamp,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
            }
            if (lastMsg != null) {
                val preview = if (lastMsg.fromOwner) "You: ${lastMsg.text}" else lastMsg.text
                Text(
                    text = preview,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// ---- Thread ----

@Composable
private fun ConversationThread(
    conversation: Conversation,
    gateState: GateState?,
    onTriggerTap: () -> Unit
) {
    val messages = conversation.messages
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        if (messages.isNotEmpty()) listState.scrollToItem(messages.size + 1)
    }

    Column(modifier = Modifier.fillMaxSize().background(DarkBackground)) {

        // Thread header
        Surface(color = DarkSurface, modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val initial = conversation.displayName.firstOrNull { it.isLetter() }
                    ?.uppercaseChar()?.toString() ?: "?"
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(Primary.copy(alpha = 0.2f), RoundedCornerShape(50)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(initial, fontWeight = FontWeight.Bold, color = Primary)
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        conversation.displayName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = OnDarkBackground
                    )
                    Text("Mobile", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                }
            }
        }

        // Message list
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

        // MG1 banner — Aina's thread only
        if (gateState != null) {
            Surface(color = DarkSurface, modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
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
                                GateState.UNLOCKED  -> "Affection or red flag? →"
                                GateState.COMPLETED -> "Play again"
                                GateState.LOCKED    -> "I can't make sense of this yet"
                            }
                        )
                    }
                }
            }
        }
    }
}

// ---- Shared bubble composables ----

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
                            Receipt.READ      -> "Read"
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
