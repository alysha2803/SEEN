package com.example.seen.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.example.seen.data.SearchEntry
import com.example.seen.ui.theme.*

@Composable
fun BrowserScreen(searches: List<SearchEntry>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Fake search bar (non-interactive)
        Surface(color = DarkSurface, modifier = Modifier.fillMaxWidth().padding(12.dp), shape = RoundedCornerShape(24.dp)) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
                Text("Search history", style = MaterialTheme.typography.bodyMedium, color = TextMuted, fontStyle = FontStyle.Italic)
            }
        }

        Text(
            text = "Recent searches",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(searches, key = { it.id }) { entry ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        if (entry.isPeopleSearch) Icons.Default.Warning else Icons.Default.Search,
                        contentDescription = null,
                        tint = if (entry.isPeopleSearch) Primary else TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(entry.query, style = MaterialTheme.typography.bodyMedium, color = OnDarkBackground)
                        if (entry.isPeopleSearch) {
                            Text(
                                "People-search result",
                                style = MaterialTheme.typography.labelSmall,
                                color = Primary
                            )
                        }
                    }
                }
                HorizontalDivider(color = Divider)
            }
        }
    }
}
