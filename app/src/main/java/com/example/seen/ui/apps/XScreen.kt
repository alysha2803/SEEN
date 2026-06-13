package com.example.seen.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.seen.data.Tweet
import com.example.seen.ui.theme.*

@Composable
fun XScreen(tweets: List<Tweet>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        item {
            Text(
                text = "Pinned",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
            )
        }
        items(tweets.take(1), key = { it.id }) { tweet ->
            TweetCard(tweet = tweet, pinned = true)
        }
        item { HorizontalDivider(color = Divider) }
        items(tweets.drop(1), key = { it.id }) { tweet ->
            TweetCard(tweet = tweet, pinned = false)
            HorizontalDivider(color = Divider)
        }
    }
}

@Composable
private fun TweetCard(tweet: Tweet, pinned: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Primary.copy(alpha = 0.15f), RoundedCornerShape(50)),
            contentAlignment = Alignment.Center
        ) {
            Text("A", fontWeight = FontWeight.Bold, color = Primary)
        }
        Column {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(tweet.handle, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = OnDarkBackground)
                if (pinned) {
                    Surface(color = Primary.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                        Text("Pinned", style = MaterialTheme.typography.labelSmall, color = Primary, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                    }
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(tweet.text, style = MaterialTheme.typography.bodyMedium, color = OnDarkBackground)
        }
    }
}
