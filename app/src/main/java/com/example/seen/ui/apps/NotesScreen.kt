package com.example.seen.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.seen.data.GateState
import com.example.seen.data.NoteEntry
import com.example.seen.ui.theme.*

@Composable
fun NotesScreen(
    notes: List<NoteEntry>,
    gateState: GateState,
    onTriggerTap: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(notes, key = { it.id }) { note ->
            val isAnnotated = note.annotatedImageKey != null
            Surface(
                color = DarkSurface,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (isAnnotated) Modifier.clickable(onClick = onTriggerTap) else Modifier)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(note.title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = OnDarkBackground)
                    Spacer(Modifier.height(6.dp))
                    Text(note.body, style = MaterialTheme.typography.bodyMedium, color = OnDarkBackground.copy(alpha = 0.85f))
                    if (note.annotatedImageKey != null) {
                        Spacer(Modifier.height(10.dp))
                        AsyncImage(
                            model = "file:///android_asset/images/${note.annotatedImageKey}.png",
                            contentDescription = "Annotated post",
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = if (gateState == GateState.LOCKED) "Tap to analyse (locked)" else "Tap to trace it back →",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (gateState == GateState.LOCKED) TextMuted else Primary
                        )
                    }
                }
            }
        }
    }
}
