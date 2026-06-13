package com.example.seen.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.seen.data.MapPin
import com.example.seen.ui.theme.*
import kotlin.math.roundToInt

// No maps SDK. Static image only; pins are percentage-positioned overlays.
@Composable
fun MapsScreen(staticImageKey: String, pins: List<MapPin>) {
    Column(modifier = Modifier.fillMaxSize().background(DarkBackground)) {
        // Saved-places list
        Surface(color = DarkSurface) {
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Text(
                    "Saved places",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
                pins.forEach { pin ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Primary, modifier = Modifier.size(20.dp))
                        Text(pin.label, style = MaterialTheme.typography.bodyMedium, color = OnDarkBackground)
                    }
                }
            }
        }

        // Static map with correctly-positioned pin overlays
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            val mapWidthPx = constraints.maxWidth
            val mapHeightPx = constraints.maxHeight

            AsyncImage(
                model = "file:///android_asset/images/$staticImageKey.png",
                contentDescription = "Map",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Percentage-positioned pins; offset{} lambda has Density as receiver,
            // so 16.dp.roundToPx() is valid there without needing LocalDensity.
            pins.forEach { pin ->
                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                (mapWidthPx * pin.xPct).roundToInt() - 16.dp.roundToPx(),
                                (mapHeightPx * pin.yPct).roundToInt() - 16.dp.roundToPx()
                            )
                        }
                        .size(32.dp)
                        .background(Primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        pin.label.first().toString(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnDarkBackground
                    )
                }
            }
        }
    }
}
