package com.example.seen.ui.apps

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    items: List<GalleryItem>,
    onSetBackOverride: ((() -> Unit)?) -> Unit
) {
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    if (selectedIndex != null) {
        DisposableEffect(Unit) {
            onSetBackOverride { selectedIndex = null }
            onDispose { onSetBackOverride(null) }
        }
        BackHandler { selectedIndex = null }
        FullImageViewer(items = items, startIndex = selectedIndex!!)
    } else {
        GalleryGrid(
            albumTitle = albumTitle,
            itemCount = itemCount,
            items = items,
            onItemTap = { selectedIndex = it }
        )
    }
}

@Composable
private fun GalleryGrid(
    albumTitle: String,
    itemCount: Int,
    items: List<GalleryItem>,
    onItemTap: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().background(DarkBackground)) {
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
            itemsIndexed(items, key = { _, item -> item.id }) { index, item ->
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clickable { onItemTap(index) }
                ) {
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

@Composable
private fun FullImageViewer(items: List<GalleryItem>, startIndex: Int) {
    val pagerState = rememberPagerState(initialPage = startIndex, pageCount = { items.size })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            AsyncImage(
                model = "file:///android_asset/images/${items[page].imageKey}.png",
                contentDescription = items[page].label,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Page counter badge
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(
                text = "${pagerState.currentPage + 1} / ${items.size}",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.85f)
            )
        }
    }
}
