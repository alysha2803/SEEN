package com.example.seen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.seen.audio.LocalSoundManager
import com.example.seen.audio.SoundEffect
import com.example.seen.audio.SoundManager
import com.example.seen.nav.SeenNavHost
import com.example.seen.state.ProgressViewModel
import com.example.seen.ui.theme.DarkBackground
import com.example.seen.ui.theme.OnDarkBackground
import com.example.seen.ui.theme.SeenTheme

class MainActivity : ComponentActivity() {

    private lateinit var soundManager: SoundManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        soundManager = SoundManager(this)
        enableEdgeToEdge()
        setContent {
            SeenTheme {
                CompositionLocalProvider(LocalSoundManager provides soundManager) {
                    SeenApp()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::soundManager.isInitialized) soundManager.resumeBgMusic()
    }

    override fun onPause() {
        super.onPause()
        if (::soundManager.isInitialized) soundManager.pauseBgMusic()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::soundManager.isInitialized) soundManager.release()
    }
}

@Composable
fun SeenApp(vm: ProgressViewModel = viewModel()) {
    val navController = rememberNavController()
    val monologueQueue by vm.monologueQueue.collectAsState()
    val isLoaded by vm.isLoaded.collectAsState()
    val sound = LocalSoundManager.current

    // Play a notification pop whenever new monologue lines appear
    LaunchedEffect(monologueQueue.isEmpty()) {
        if (monologueQueue.isNotEmpty()) sound?.play(SoundEffect.NOTIFICATION_POP)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Hold on a black screen until DataStore emits so startDestination is computed
        // from the real persisted state, not the ProgressState() default.
        if (!isLoaded) return@Box

        SeenNavHost(navController = navController, vm = vm)

        // Monologue overlay sits on top of every screen
        if (monologueQueue.isNotEmpty()) {
            MonologueOverlay(
                line = monologueQueue.first(),
                onAdvance = { vm.advanceMonologue() },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun MonologueOverlay(
    line: String,
    onAdvance: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onAdvance)
            .padding(horizontal = 16.dp, vertical = 24.dp),
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        tonalElevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = line,
                style = MaterialTheme.typography.bodyLarge,
                color = OnDarkBackground
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "tap to continue",
                style = MaterialTheme.typography.labelSmall,
                color = OnDarkBackground.copy(alpha = 0.5f)
            )
        }
    }
}
