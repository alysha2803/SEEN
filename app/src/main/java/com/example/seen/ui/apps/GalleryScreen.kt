package com.example.seen.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.seen.data.GalleryItem
import com.example.seen.ui.theme.*

@Composable
fun GalleryScreen(
    albumTitle: String,
    itemCount: Int,
    items: List<GalleryItem>
) {
    Column(modifier = Modifier.fillMaxSize().background(DarkBackground)) {
        // Album header
        Surface(color = DarkSurface, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(albumTitle, style = MaterialTheme.typography.titleLarge, color = Primary, fontWeight = FontWeight.SemiBold)
                Text("$itemCount items", style = MaterialTheme.typography.bodySmall, color = TextMuted)
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            contentPadding = PaddingValues(2.dp)
        ) {
            items(items, key = { it.id }) { item ->
                Box(modifier = Modifier.aspectRatio(1f)) {
                    AsyncImage(
                        model = "file:///android_asset/images/${item.imageKey}.png",
                        contentDescription = item.label,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    if (item.label.startsWith("+")) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .background(Primary.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
                        ) {
                            Text(
                                item.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = OnDarkBackground,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
