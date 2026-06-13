package com.example.seen.ui.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.seen.data.AppId
import com.example.seen.state.ProgressState
import com.example.seen.ui.theme.DarkBackground
import com.example.seen.ui.theme.OnDarkBackground

private data class AppIcon(
    val appId: AppId,
    val label: String,
    val icon: ImageVector?,
    val tint: Color
)

private val HOME_APPS = listOf(
    AppIcon(AppId.MESSAGES, "Messages", Icons.Default.Message, Color(0xFF1E88E5)),
    AppIcon(AppId.PHONE, "Phone", Icons.Default.Phone, Color(0xFF43A047)),
    AppIcon(AppId.GALLERY, "Gallery", Icons.Default.PhotoLibrary, Color(0xFF7B1FA2)),
    AppIcon(AppId.NOTES, "Notes", Icons.Default.StickyNote2, Color(0xFFFFB300)),
    AppIcon(AppId.INSTAGRAM, "Instagram", Icons.Default.CameraAlt, Color(0xFFE91E8C)),
    AppIcon(AppId.X, "X", null, Color(0xFF212121)),
    AppIcon(AppId.BROWSER, "Browser", Icons.Default.Language, Color(0xFF4285F4)),
    AppIcon(AppId.MAPS, "Maps", Icons.Default.Map, Color(0xFF0F9D58)),
    AppIcon(AppId.MONITOR, "", null, Color(0xFF424242)),
)

@Composable
fun HomeScreen(
    progressState: ProgressState,
    onAppTap: (AppId) -> Unit,
    onHelp: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        AsyncImage(
            model = "file:///android_asset/images/wallpaper_home.png",
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            alpha = 0.4f
        )

        // Status bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("2:14", fontSize = 14.sp, color = OnDarkBackground)
            IconButton(onClick = onHelp, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.Help, contentDescription = "Leave / get help", tint = OnDarkBackground.copy(alpha = 0.7f))
            }
        }

        // App grid anchored to bottom
        val rows = HOME_APPS.chunked(3)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            rows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    row.forEach { app ->
                        AppIconCell(app = app, onClick = { onAppTap(app.appId) })
                    }
                    // Pad incomplete last row
                    repeat(3 - row.size) {
                        Box(modifier = Modifier.size(72.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun AppIconCell(app: AppIcon, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(app.tint, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (app.icon != null) {
                Icon(app.icon, contentDescription = app.label, tint = Color.White, modifier = Modifier.size(28.dp))
            } else if (app.label == "X") {
                Text("X", fontWeight = FontWeight.Black, fontSize = 22.sp, color = Color.White)
            }
        }
        if (app.label.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            Text(app.label, style = MaterialTheme.typography.labelSmall, color = OnDarkBackground, maxLines = 1)
        }
    }
}
