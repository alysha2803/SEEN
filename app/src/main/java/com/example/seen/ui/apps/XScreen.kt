package com.example.seen.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.seen.data.Tweet
import com.example.seen.ui.theme.*

@Composable
fun XScreen(
    handle: String,
    displayName: String,
    bio: String,
    location: String,
    following: String,
    followers: String,
    tweets: List<Tweet>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Profile header
        item {
            XProfileHeader(
                handle = handle,
                displayName = displayName,
                bio = bio,
                location = location,
                following = following,
                followers = followers,
                postCount = tweets.size
            )
        }

        // Pinned label + first tweet
        item {
            Text(
                text = "Pinned",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                modifier = Modifier.padding(start = 20.dp, top = 12.dp, bottom = 2.dp)
            )
        }
        items(tweets.take(1), key = { it.id }) { tweet ->
            TweetRow(tweet = tweet, pinned = true)
            HorizontalDivider(color = Divider)
        }

        // Remaining tweets
        items(tweets.drop(1), key = { it.id }) { tweet ->
            TweetRow(tweet = tweet, pinned = false)
            HorizontalDivider(color = Divider)
        }
    }
}

@Composable
private fun XProfileHeader(
    handle: String,
    displayName: String,
    bio: String,
    location: String,
    following: String,
    followers: String,
    postCount: Int
) {
    Surface(color = DarkSurface) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Banner strip
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(Primary.copy(alpha = 0.08f))
            )

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                // Avatar — overlaps the banner
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .offset(y = (-20).dp)
                        .background(Primary.copy(alpha = 0.2f), RoundedCornerShape(50)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "A",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                }

                Spacer(Modifier.height((-12).dp))

                // Display name + handle
                Text(
                    displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnDarkBackground
                )
                Text(
                    "@$handle",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )

                Spacer(Modifier.height(8.dp))

                // Bio
                Text(
                    bio,
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnDarkBackground
                )

                Spacer(Modifier.height(6.dp))

                // Location
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(location, style = MaterialTheme.typography.bodySmall, color = TextMuted)
                }

                Spacer(Modifier.height(10.dp))

                // Stats row
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    StatChip(value = following, label = "Following")
                    StatChip(value = followers, label = "Followers")
                    StatChip(value = postCount.toString(), label = "Posts")
                }

                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun StatChip(value: String, label: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = OnDarkBackground)
        Text(label, style = MaterialTheme.typography.bodySmall, color = TextMuted)
    }
}

@Composable
private fun TweetRow(tweet: Tweet, pinned: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    tweet.handle,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = OnDarkBackground
                )
                if (pinned) {
                    Surface(color = Primary.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                        Text(
                            "Pinned",
                            style = MaterialTheme.typography.labelSmall,
                            color = Primary,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(tweet.text, style = MaterialTheme.typography.bodyMedium, color = OnDarkBackground)
        }
    }
}
