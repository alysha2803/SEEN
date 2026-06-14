package com.example.seen.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage
import com.example.seen.ui.theme.Primary

@Composable
fun ContactAvatar(
    avatarKey: String,
    displayName: String,
    size: Dp,
    modifier: Modifier = Modifier
) {
    val initial = displayName.firstOrNull { it.isLetter() }?.uppercaseChar()?.toString() ?: "?"
    val textStyle = when {
        size.value >= 56f -> MaterialTheme.typography.titleLarge
        size.value >= 40f -> MaterialTheme.typography.bodyLarge
        else -> MaterialTheme.typography.bodyMedium
    }
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Primary.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        Text(initial, style = textStyle, fontWeight = FontWeight.Bold, color = Primary)
        AsyncImage(
            model = "file:///android_asset/images/$avatarKey.png",
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
        )
    }
}
