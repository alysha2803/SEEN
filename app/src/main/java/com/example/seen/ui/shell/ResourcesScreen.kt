package com.example.seen.ui.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.seen.data.ContentRepository
import com.example.seen.ui.theme.DarkBackground
import com.example.seen.ui.theme.Divider
import com.example.seen.ui.theme.OnDarkBackground
import com.example.seen.ui.theme.Primary
import com.example.seen.ui.theme.TextMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourcesScreen(
    onBack: () -> Unit,
    showReplay: Boolean = false,
    onReplay: () -> Unit = {}
) {
    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Get help", color = OnDarkBackground) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = OnDarkBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item {
                Text(
                    text = "If you or someone you know is being stalked or harassed:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnDarkBackground.copy(alpha = 0.7f),
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }

            items(ContentRepository.resources) { resource ->
                Column(modifier = Modifier.padding(vertical = 12.dp)) {
                    Text(
                        text = resource.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = Primary
                    )
                    Text(
                        text = resource.detail,
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnDarkBackground
                    )
                }
                HorizontalDivider(color = Divider)
            }

            item {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Verify all contact details are current before relying on them.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }

            if (showReplay) {
                item {
                    Spacer(Modifier.height(24.dp))
                    HorizontalDivider(color = Divider)
                    Spacer(Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = onReplay,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp)
                    ) {
                        Text("Play again from the start", color = OnDarkBackground)
                    }
                }
            } else {
                item { Spacer(Modifier.height(32.dp)) }
            }
        }
    }
}
